package io.github.admiral.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;

/** LRU cache complementation.*/
public class LRUCache<T> {

    /** Node complementation.*/
    private class Node<T> {
        public Node<T> next;
        public Node<T> prev;
        public T data;

        public Node(Node<T> next, Node<T> prev, T data) {
            this.next = next;
            this.prev = prev;
            this.data = data;
        }

        public Node(T data) {
            this(null, null, data);
        }
    }

    private final int capacity;
    private final Node<T> head;
    private int size = 0;
    private Node<T> tail;
    private Map<T, Node<T>> map = new ConcurrentHashMap<>(16);
    private final Lock lock = new ReentrantLock();

    public LRUCache(int capacity) {
        this.capacity = capacity;
        head = new Node<>(null, null, null);
    }

    public int getSize() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean put(T data){
        return true;
    }
}
