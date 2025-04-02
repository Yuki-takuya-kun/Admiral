package io.github.admiral.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class RingQueueTest {
    RingQueue<Integer> rq = new RingQueue<>(10);
    volatile AtomicInteger counter = new AtomicInteger(0);
    @Test
    public void testRingQueueSingleThread(){
        for (int i = 0; i < 100; i++) {
            Integer r = rq.offer(i);
            if (i < 10) assertTrue(r == null);
            else assertTrue(r == i - 10);
        }
    }

    @Test
    public void testRingQueueMultiThread(){
        rq.clear();
        int base = 1_000;
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < base; i++) {
                Integer r = rq.offer(i);
                //System.out.println(r);
                if (r != null) counter.addAndGet(1);
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < base; i++) {
                Integer r = rq.offer(i);
                if (r != null) counter.addAndGet(1);
            }
        });
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(base*2-10, counter.get());
    }
}