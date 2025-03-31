package io.github.admiral.communicate;

/***
 * Interface of communication between different parts.
 */
public interface SignalCorp {
    /** Receive message from other signal corp.*/
    void receive(Message message);

    /** Send message to other signal corp.*/
    boolean send(Message message);
}
