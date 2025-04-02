package io.github.admiral.admiral;

import io.github.admiral.common.*;
import io.github.admiral.hr.Troop;

import java.util.*;

public class AdmiralChiefOfStaff implements ChiefOfStaff {

    /** See Admiral.childs*/
    private volatile Map<Troop, Set<Troop>> childs;

    /** See Admiral.parents*/
    private volatile Map<Troop, Set<Troop>> parents;

    /** See Admiral.rootTroops*/
    private volatile Set<Troop> rootTroops;

    private final TraceRepository traceRepository = TraceRepository.getInstance();

    public AdmiralChiefOfStaff(Map<Troop, Set<Troop>> childs, Map<Troop, Set<Troop>> parents,
                               Set<Troop> rootTroops) {
        this.childs = childs;
        this.parents = parents;
        this.rootTroops = rootTroops;
    }

    @Override
    public boolean isEnd(RequestInfo requestInfo) {
        return traceRepository.getOutDatedSet().contains(requestInfo);
    }

    /** While complete a troop, using this to find next runnable troops. */
    @Override
    public List<Troop> nextTroop(final RequestInfo requestInfo, final Troop troop) {
        List<Troop> nextTroops = new ArrayList<>();

        // out dated
        if (traceRepository.getOutDatedSet().contains(requestInfo)) {
            return nextTroops;
        }

        // start a new request
        if (rootTroops.contains(troop)) {
            Trace trace = new Trace(requestInfo, troop);
            for (Troop child: childs.get(troop)){
                if (parents.get(troop).size() == 1){
                    trace.addRunningTroop(child);
                    nextTroops.add(child);
                }
                else trace.addWaitingTroop(troop);
            }
            traceRepository.addTrace(requestInfo, trace);
            return nextTroops;
        }

        // if the request is running
        Trace trace = traceRepository.getTrace(requestInfo);
        synchronized (trace){
            try {
                trace.completeTroop(troop);
                for (Troop child: childs.get(troop)){
                    if (runTroop(child, trace)) nextTroops.add(child);
                }
            }
            catch (TroopNotFoundException e) {
                e.printStackTrace();
            }

            // finally, if running troops set is empty, then the task is end
            if (!trace.isRunning()) {
                traceRepository.setRequestOutdated(requestInfo);
            }
        }
        return nextTroops;
    }

    /** If the troop execute is failed, also announce trace.*/
    public void troopFailed(final RequestInfo requestInfo, final Troop troop, final ExceptionInfo exceptionInfo) {
        Trace trace = traceRepository.getTrace(requestInfo);
        try {
            trace.addErrorTroop(troop, exceptionInfo);
        } catch (TroopNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** Assert if the troop is runnable in the next troops if complete the troop.
     * On the same time, it will decrease parent counter and add new children.
     * @param troop The troop that finished.
     * @param trace The trace structure.
     *
     * @return Boolean flag that signify if the troop can be run at next time.
     * */
    private boolean runTroop(final Troop troop, final Trace trace) throws TroopNotFoundException {
        // if the troop not in the waiting troop, add as new waiting troop.
        if (!trace.isWaiting(troop)) {
            trace.addWaitingTroop(troop);
            return false;
        }

        // if the troop in waiting troop and parent count is 1, transfer it to running troop.
        if (trace.getWaitingSize(troop) == 1) {
            trace.transferWaiting2Running(troop);
            return true;
        }

        // else decrement the parent counter
        trace.decreaseWaitingTroopCounter(troop);
        return false;
    }

}
