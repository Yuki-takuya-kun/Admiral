package io.github.admiral.admiral;

import io.github.admiral.common.RequestInfo;
import io.github.admiral.common.TraceRepository;
import io.github.admiral.hr.Troop;
import io.github.admiral.Admiral;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AdmiralChiefOfStaff implements ChiefOfStaff {

    /** See Admiral.childs*/
    private volatile Map<String, Set<Troop>> childs;

    /** See Admiral.parents*/
    private volatile Map<String, Set<Troop>> parents;

    /** See Admiral.events*/
    private volatile Map<String, Set<String>> events;

    /** See Admiral.rootTroops*/
    private volatile List<Troop> rootTroops = new ArrayList<>();

    private volatile TraceRepository traceRepository = TraceRepository.getInstance();

    public AdmiralChiefOfStaff() {

    }

    @Override
    public boolean isEnd(RequestInfo requestInfo) {
        return traceRepository.getOutDatedSet().contains(requestInfo);
    }

    @Override
    public List<Troop> nextTroop(RequestInfo requestInfo, Troop troop){
        List<Troop> nextTroops = new ArrayList<>();

        if (traceRepository.getRepo().containsKey(requestInfo)){

        }
        // out date
        else if (traceRepository.getOutDatedSet().contains(requestInfo)){

        }
        // start of a new request
        else if (rootTroops.contains(troop)){

        }
        return nextTroops;
    }

    /** A data structure that contain tracing graph.*/
    private static class Trace {
        /** Count how many troop before given troop have not been executed in execution graph.*/
        public Map<Troop, Integer> waitingTroop = new ConcurrentHashMap<>();

        /** Reserve how many troop is running.*/
        public Set<Troop> runningTroops = ConcurrentHashMap.newKeySet();

    }
}
