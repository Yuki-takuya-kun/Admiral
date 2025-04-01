package io.github.admiral.communicate;

/***
 * Interface of communication between different parts.
 */
public abstract class SignalCorp<R, S> {
    /** Receive message from other signal corp.*/
    public abstract void receive(R message);

    /** Send message to other signal corp.*/
    public abstract void send(S message);
}
