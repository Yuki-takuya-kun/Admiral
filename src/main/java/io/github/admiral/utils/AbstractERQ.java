package io.github.admiral.utils;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/** Abstract eliminable ring queue.*/
public abstract class AbstractERQ<T> implements EliminableRingQueue<T>{

    /** Queue that conserve the elements.*/
    protected volatile T[] queue;

    /** Head of the queue.*/
    protected volatile int front;

    /** Tail of the queue.*/
    protected volatile int rear;

    /** Size of elements in the queue.*/
    protected volatile int size;

    /** Capacity of the queue.*/
    protected volatile int capacity;

    /** Lock considering thread safe.*/
    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public AbstractERQ(int capacity) {
        this.capacity = capacity;
        queue = (T[]) new Object[capacity];
        front = 0;
        rear = 0;
        size = 0;
    }
}
