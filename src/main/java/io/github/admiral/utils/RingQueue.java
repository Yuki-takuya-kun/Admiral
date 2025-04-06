package io.github.admiral.utils;

import java.util.AbstractQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.lang.Nullable;

/** Ring queue implementation.*/
public class RingQueue<T> extends AbstractERQ<T> {


    public RingQueue(int capacity) {
        super(capacity);
    }

    /** Enqueue an element into ring queue, if the queue is full, then it will return the first elem.
     * Or it will return null.
     *
     * @param element element that needed to enqueue.
     * */
    public T offer(T element) {
        lock.writeLock().lock();
        try {
            T elem = null;
            if (size == capacity){
               elem = poll();
            }
            queue[rear] = element;
            size ++;
            rear = (rear + 1) % capacity;
            return elem;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /** This function is only support for enQueue function, because we do not need to dequeue elements by hand.*/
    public T poll() {
        lock.writeLock().lock();
        try {
            if (size == 0) return null;
            T elem = queue[front];
            front = (front + 1) % capacity;
            size --;
            return elem;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getSize(){
        return size;
    }

    /** Clear all elements in the ring queue.*/
    public void clear(){
        lock.writeLock().lock();
        try {
            front = rear;
            size = 0;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
