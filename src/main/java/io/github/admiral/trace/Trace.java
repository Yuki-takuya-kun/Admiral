package io.github.admiral.trace;

import io.github.admiral.admiral.IllegalTroopException;
import io.github.admiral.common.ExceptionInfo;
import io.github.admiral.common.RequestInfo;
import io.github.admiral.hr.BaseTroop;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** A structure that conserve tracing data.*/
public class Trace {
    /** Request information that creating the Trace.*/
    private RequestInfo requestInfo;

    /** Count how many troop before given troop have not been executed in execution graph.*/
    private Map<BaseTroop, Integer> waitingTroops = new ConcurrentHashMap<BaseTroop, Integer>();

    /** Reserve how many troop is running.*/
    private Set<BaseTroop> runningTroops = ConcurrentHashMap.newKeySet();

    /** Reserve completed troops, key is troop, value is complete time.*/
    private Map<BaseTroop, Long> completeTroops = new ConcurrentHashMap();

    /** Reserve error troops, key is troop, value is why the troop is error.*/
    private Map<BaseTroop, ExceptionInfo> errorTroops = new ConcurrentHashMap();

    public Trace(RequestInfo requestInfo, BaseTroop troop){
        this.requestInfo = requestInfo;
        completeTroops.put(troop, System.currentTimeMillis());
    }

    public void addRunningTroop(final BaseTroop troop) {
        runningTroops.add(troop);
    }

    /** Complete the troop. */
    public void completeTroop(final BaseTroop troop) throws TroopNotFoundException{
        if (!runningTroops.contains(troop)) {throw new TroopNotFoundException(troop, " does not running.");}
        removeRunningTroop(troop);
        completeTroops.put(troop, System.currentTimeMillis());
    }

    public void removeRunningTroop(final BaseTroop troop) throws TroopNotFoundException{
        if (!runningTroops.contains(troop)) { throw new TroopNotFoundException(troop, "does not running.");}
        runningTroops.remove(troop);
    }

    /** Assert the troop is waiting or not. */
    public boolean isWaiting(final BaseTroop troop) {
        return waitingTroops.containsKey(troop);
    }

    /** Get waiting how many parent completed for the troop.*/
    public int getWaitingSize(final BaseTroop troop) throws TroopNotFoundException {
        if (!waitingTroops.containsKey(troop)) {throw new TroopNotFoundException(troop, "not in waiting queue.");}
        return waitingTroops.get(troop);
    }

    /** Add a troop to waiting troop map, the value of it */
    public void addWaitingTroop(final BaseTroop troop) {
        if (troop.getSubscribes().length == 1) throw new IllegalTroopException(troop, "Troop %s is already runnable, do not add it to waiting queue.");
        waitingTroops.put(troop, troop.getSubscribes().length-1);
    }

    /** Remove troop from waiting queue.*/
    public void removeWaitingTroop(final BaseTroop troop) {
        waitingTroops.remove(troop);
    }

    /** Move a troop from waiting queue to running queue.*/
    public void transferWaiting2Running(final BaseTroop troop) throws TroopNotFoundException {
        if (!waitingTroops.containsKey(troop)) { throw new TroopNotFoundException(troop);}
        removeWaitingTroop(troop);
        addRunningTroop(troop);
    }

    /** Decrease the waiting counter for the troop.*/
    public void decreaseWaitingTroopCounter(final BaseTroop troop) throws TroopNotFoundException {
        if (!waitingTroops.containsKey(troop)) { throw new TroopNotFoundException(troop); }
        if (!waitingTroops.get(troop).equals(1)) { throw new IllegalArgumentException("The troop is runnable, please do not decrement it."); }
        waitingTroops.put(troop, waitingTroops.get(troop) - 1);
    }

    /** Add a troop to the error troop. Only if the troop is in running troop.*/
    public void addErrorTroop(final BaseTroop troop, ExceptionInfo error) throws TroopNotFoundException {
        if (!runningTroops.contains(troop)) { throw new TroopNotFoundException(troop, " not in running queue.");}
        removeRunningTroop(troop);
        errorTroops.put(troop, error);
    }

    /** Assert if there are running process.*/
    public boolean isRunning(){
        return !runningTroops.isEmpty();
    }
}
