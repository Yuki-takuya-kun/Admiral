package io.github.admiral.trace;

import io.github.admiral.common.RequestInfo;
import io.github.admiral.utils.RingQueue;
import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** A singleton object that conserve all trace and will be shared by all chief of staff, singleton.*/
@Getter
public class TraceRepository {
    /** Reserve all valid trace.*/
    private volatile Map<RequestInfo, Trace> repo = new ConcurrentHashMap<>();

    /** Reserve all running request info and out date timestamp, including complete and error request.*/
    private volatile RingQueue<RequestInfo> requestQueue = new RingQueue<>(10_000);

    /** Reserve all out dated request using set for improve searching performance.*/
    private volatile Set<RequestInfo> outDatedSet = ConcurrentHashMap.newKeySet();

    private TraceRepository() {}

    public static TraceRepository getInstance() {
        return TraceRepoSingleton.INSTANCE;
    }

    private static class TraceRepoSingleton {
        private static final TraceRepository INSTANCE = new TraceRepository();
    }

    public void addTrace(final RequestInfo request, final Trace trace) {
        repo.put(request, trace);
    }

    public boolean requestIsRunning(final RequestInfo request) {
        return repo.containsKey(request);
    }

    /** Set request is outdated while the request is complete or break for error.*/
    public void setRequestOutdated(final RequestInfo request){
        repo.remove(request);
        outDatedSet.add(request);
    }

    /** Get trace given request info. */
    public Trace getTrace(final RequestInfo request) {
        return repo.get(request);
    }
}
