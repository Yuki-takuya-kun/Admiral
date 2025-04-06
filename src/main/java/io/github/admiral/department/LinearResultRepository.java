package io.github.admiral.department;

import io.github.admiral.utils.RingQueue;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Using linear elimination strategy. Which means when data reach capacity, */
public class LinearResultRepository extends ResultRepository {

    /** Key is request id and task name. value is result.*/
    private final Map<String, Result> resultMap = new ConcurrentHashMap<>();

    /** key is request id and value is put time.*/
    private final Map<String, Long> timeMap = new ConcurrentHashMap<>();

    /** Capacity of the queue.*/
    private final int capacity;

    /** Ring queue with key and put time.*/
    private final RingQueue<Pair<String, Long>> queue;

    public LinearResultRepository(int capacity) {
        this.capacity = capacity;
        queue = new RingQueue<>(capacity);
    }

    /** Transfer requestId and name to key.*/
    private String keyTransform(String requestId, String name){
        return requestId + "_" + name;
    }

    @Override
    public Result get(String requestId, String name) {
        return resultMap.get(keyTransform(requestId, name));
    }

    @Override
    public void put(Result result) {
        String requestId = result.getRequestId();
        String name = result.getTaskName();
        String key = keyTransform(requestId, name);
        Long time = System.currentTimeMillis();
        Pair<String, Long> outDated = queue.offer(Pair.of(key, time));
        if (outDated != null){
            String outDatedKey = outDated.getKey();
            resultMap.remove(outDatedKey);
            timeMap.remove(outDatedKey);
        }
        resultMap.put(key, result);
    }

}
