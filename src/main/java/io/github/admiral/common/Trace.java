package io.github.admiral.common;

import io.github.admiral.hr.Troop;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** A structure that conserve tracing data.*/
public class Trace {
    /** Count how many troop before given troop have not been executed in execution graph.*/
    public Map<Troop, Integer> watiingTroop = new ConcurrentHashMap<Troop, Integer>();

    /** Reserve how many troop is running.*/
    public Set<Troop> runningTroops = ConcurrentHashMap.newKeySet();
}
