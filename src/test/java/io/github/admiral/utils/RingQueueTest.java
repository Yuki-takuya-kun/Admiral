package io.github.admiral.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class RingQueueTest {

    @Test
    public void testRingQueueSingleThread(){
        int base = 100;
        RingQueue<Integer> rq = new RingQueue<>(base);
        for (int i = 0; i < base; i++) {
            rq.offer(i);
        }
        assertEquals(base, rq.getSize());
    }

    @Test
    public void testRingQueueMultiThread(){
        int base = 1000;
        RingQueue<Integer> rq = new RingQueue<>(base);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < base/2; i++) {
                rq.offer(i);
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < base/2; i++) {
                rq.offer(i);
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
        assertEquals(base/2*2, rq.getSize());
    }
}