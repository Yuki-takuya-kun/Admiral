package io.github.admiral.common;

import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** A singleton object that conserve all trace and will be shared by all chief of staff, singleton.*/
@Getter
public class TraceRepository {
    /** Reserve all valid trace.*/
    private volatile Map<RequestInfo, Trace> repo = new ConcurrentHashMap<>();

    /** Reserve all out dated request info and out date timestamp, including complete and error request.*/
    private volatile LinkedList<Pair<RequestInfo, Long>> outDatedList = new LinkedList<>();

    /** Reserve all out dated request using set for improve searching performance.*/
    private volatile Set<RequestInfo> outDatedSet = ConcurrentHashMap.newKeySet();

    private TraceRepository() {}

    public static TraceRepository getInstance() {
        return TraceRepoSingleton.INSTANCE;
    }

    private static class TraceRepoSingleton {
        private static final TraceRepository INSTANCE = new TraceRepository();
    }
}
