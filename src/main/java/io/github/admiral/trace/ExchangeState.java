package io.github.admiral.trace;

/** Exchange state while exchange.*/
public enum ExchangeState {
    /** START of the exchange.*/
    START,
    SUCCESS,
    NETWORK_ERROR,
    JOB_NOTFOUND,
    JOB_DELETED,
    INTERNAL_ERROR
}
