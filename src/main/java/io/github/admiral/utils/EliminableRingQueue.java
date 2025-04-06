package io.github.admiral.utils;

/** Interface for eliminable ring queue. Implementations should implement offer and poll methods.*/
public interface EliminableRingQueue<T> {

    /** Add element to queue.
     *
     * @param element element that should add.
     * @return The deleted element that need to be eliminated.
     * */
    T offer(T element);

    /** Poll element from queue
     *
     * @return null if there is no elements in queue.
     * */
    T poll();

    /**
     * Get size of the queue, please attention that it will clear all elements that should be eliminated.
     * It may cause a stop of thread.
     * */
    public int getSize();
}
