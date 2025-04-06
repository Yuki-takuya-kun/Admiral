package io.github.admiral.department;

import io.github.admiral.utils.RingQueue;

import java.util.Map;

/**
 * Reserve soldier execution result, which can be got from other military department.
 * This is a abstract class, implementation should implement abstract methods using their own strategy.
 * */
public abstract class ResultRepository {

    /** Get a result using requestId and name.*/
    public abstract Result get(String requestId, String name);

    /** Put result into result repository.*/
    public abstract void put(Result result);

}
