package io.github.admiral.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeERingQueueTest {

    @Test
    public void testTimeout() {
        TimeERingQueue<Integer> queue = new TimeERingQueue<>(10, 1000);
        int base = 10;
        for (int i = 0; i < base; i++) {
            queue.offer(i);
        }

        assertEquals(base, queue.getSize());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        assertEquals(0, queue.getSize());
    }

    @Test
    public void testMultiThread(){
        int base = 1000;
        TimeERingQueue<Integer> queue = new TimeERingQueue<>(1000, 1000);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < base/2; i++) {
                queue.offer(i);
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < base/2; i++) {
                queue.offer(i);
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
        assertEquals(base/2*2, queue.getSize());
    }
}
