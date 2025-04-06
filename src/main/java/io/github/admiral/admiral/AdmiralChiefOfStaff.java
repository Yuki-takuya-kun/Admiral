package io.github.admiral.admiral;

import io.github.admiral.common.*;
import io.github.admiral.hr.BaseTroop;
import io.github.admiral.trace.Trace;
import io.github.admiral.trace.TraceRepository;
import io.github.admiral.trace.TroopNotFoundException;

import java.util.*;

public class AdmiralChiefOfStaff implements ChiefOfStaff {

    /** See Admiral.childs*/
    private final Map<BaseTroop, Set<BaseTroop>> childs;

    /** See Admiral.parents*/
    private final Map<BaseTroop, Set<BaseTroop>> parents;

    /** See Admiral.rootTroops*/
    private final Set<BaseTroop> rootTroops;

    private final TraceRepository traceRepository = TraceRepository.getInstance();

    public AdmiralChiefOfStaff(Map<BaseTroop, Set<BaseTroop>> childs, Map<BaseTroop, Set<BaseTroop>> parents,
                               Set<BaseTroop> rootTroops) {
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
    public List<BaseTroop> nextTroop(final RequestInfo requestInfo, final BaseTroop troop) throws OutdatedRequestException, IllegalTroopException {
        List<BaseTroop> nextTroops = new ArrayList<>();

        // out dated
        if (traceRepository.getOutDatedSet().contains(requestInfo)) {
            throw new OutdatedRequestException(requestInfo);
        }

        // if the troop is not the start of the root troop and the request info is unfounded, raise error.
        if (!rootTroops.contains(troop) && !traceRepository.requestIsRunning(requestInfo)) {
            throw new IllegalTroopException(requestInfo, troop,
                    "troop %s of request \"%s\" is invalid. Which do not begins with a root troop.");
        }

        // start a new request
        if (rootTroops.contains(troop)) {
            Trace trace = new Trace(requestInfo, troop);
            for (BaseTroop child: childs.get(troop)){
                if (parents.get(child).size() == 1){
                    trace.addRunningTroop(child);
                    nextTroops.add(child);
                }
                else trace.addWaitingTroop(child);
            }
            traceRepository.addTrace(requestInfo, trace);
            return nextTroops;
        }

        // if the request is running
        Trace trace = traceRepository.getTrace(requestInfo);
        synchronized (trace){
            try {
                trace.completeTroop(troop);
                for (BaseTroop child: childs.get(troop)){
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
    public void troopFailed(final RequestInfo requestInfo, final BaseTroop troop, final ExceptionInfo exceptionInfo) {
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
    private boolean runTroop(final BaseTroop troop, final Trace trace) throws TroopNotFoundException {
        // if the troop is already runnable, add it to running queue.
        if (troop.getSubscribes().length == 1){
            trace.addRunningTroop(troop);
            return true;
        }

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
