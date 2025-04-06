package io.github.admiral.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/** Using time to eliminate elements.*/
public class TimeERingQueue<T> extends AbstractERQ<T>{

    /** Record added time for each element.*/
    private final Long[] timer;

    /** Timeout for element elimination.*/
    private final long timeout;

    public TimeERingQueue(int capacity, long timeout, TimeUnit unit) {
        super(capacity);
        this.timer = new Long[capacity];
        this.timeout = unit.toMillis(timeout);
    }

    public TimeERingQueue(int capacity, long timeout) {
        super(capacity);
        this.timer = new Long[capacity];
        this.timeout = timeout;
    }

    /** Add element to queue.*/
    public T offer(T element) {
        lock.writeLock().lock();
        try {
            T elem = eliminateTimeOutElementStep();
            if (elem == null && size == capacity){
                elem = poll();
            }
            queue[rear] = element;
            timer[rear] = System.currentTimeMillis();
            size ++;
            rear = (rear + 1) % capacity;
            return elem;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /** Poll element from queue. Mention that it will eliminate all timeout elements, it may cause performance problem.*/
    public T poll(){
        lock.writeLock().lock();
        try {
            if (size == 0) return null;
            eliminateTimeOutElement();
            T elem = queue[front];
            front = (front + 1) % capacity;
            size --;
            return elem;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /** Size of the queue. Mention that it will eliminate all timeout elements, it may cause performance problem.*/
    public int getSize(){
        lock.writeLock().lock();
        try {
            eliminateTimeOutElement();
            return size;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /** Eliminate if timeout in a step. Which means only eliminate one element at most.*/
    private T eliminateTimeOutElementStep(){
        lock.writeLock().lock();
        try {
            if (size == 0) return null;
            Long insertTime = timer[front];
            Long currentTime = System.currentTimeMillis();
            if (currentTime > insertTime + timeout) {
                T elem = queue[front];
                front = (front + 1) % size;
                size --;
                return elem;
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /** Eliminate all timeout elements.*/
    private void eliminateTimeOutElement(){
        lock.writeLock().lock();
        try {
            Long currentTime = System.currentTimeMillis();
            while (size > 0 && currentTime > timer[front] + timeout){
                front = (front + 1) % capacity;
                size --;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }


}
