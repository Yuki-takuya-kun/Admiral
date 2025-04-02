package io.github.admiral.utils;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.lang.Nullable;

/** Ring queue implementation.*/
public class RingQueue<T> {
    /** Queue that conserve the elements.*/
    private volatile T[] queue;

    /** Head of the queue.*/
    private volatile int front;

    /** Tail of the queue.*/
    private volatile int rear;

    /** Size of elements in the queue.*/
    private volatile int size;

    /** Capacity of the queue.*/
    private volatile int capacity;

    /** Lock considering thread safe.*/
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public RingQueue(int capacity) {
        this.capacity = capacity;
        queue = (T[]) new Object[capacity];
        front = 0;
        rear = 0;
        size = 0;
    }

    /** Enqueue a element into ring queue, if the queue is full, then it will return the first elem.
     * Or it will return null.
     *
     * @param element element that needed to enqueue.
     * */
    public @Nullable T offer(T element) {
        lock.writeLock().lock();
        try {
            T elem = null;
            if (isFull()){
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

    /** Enqueue a element into ring queue, if the queue is full, then it will return the first elem.
     * Or it will return null.
     *
     * @param element element that needed to enqueue.
     * */
    public @Nullable T offerLast(T element) {
        return offer(element);
    }

    /** Get last element in the queue.*/
    public @Nullable T peekLast() {
        lock.readLock().lock();
        try {
            return queue[rear];
        } finally {
            lock.readLock().unlock();
        }
    }

    /** Get first element in the queue.*/
    public @Nullable T peekFirst(){
        lock.readLock().lock();
        try {
            return queue[front];
        } finally {
            lock.readLock().unlock();
        }
    }

    /** This function is only support for enQueue function, because we do not need to dequeue elements by hand.*/
    public @Nullable T poll() {
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

    /** This function is only support for enQueue function, because we do not need to dequeue elements by hand.*/
    public @Nullable T pollFirst(){
        return poll();
    }

    /** Assert if the ring queue is full.*/
    public boolean isFull(){return size == capacity;}

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
