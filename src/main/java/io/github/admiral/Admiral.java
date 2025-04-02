package io.github.admiral;

import io.github.admiral.hr.HumanResource;
import io.github.admiral.hr.Troop;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Admiral is a director of how to schedule all the execution graph flows correctly.
 *
 * @author Jiahao Hwang
 */
@Component
@Slf4j
public class Admiral {
    /** Record troops.*/
    private volatile Set<Troop> troops = ConcurrentHashMap.newKeySet();

    /** Record event is produced by what troops. Key is troop, Value is set of troops that name the event.*/
    private volatile Map<Troop, Set<Troop>> parents = new ConcurrentHashMap<>();

    /** Record event is consumed by what troops. Key is troop, Value is set of troops that consume the event.*/
    private volatile Map<Troop, Set<Troop>> childs = new ConcurrentHashMap<>();

    /** Record root troops. Each root soldierFile will maintain a message queue that can poll message for each completed
     * task. The index of rootService is the partition index in message queue.*/
    private volatile List<Troop> rootTroops = new CopyOnWriteArrayList<>();

    /** Record removed troops that incur any partition is idle, reuse them.*/
    private volatile Queue<Integer> idleList = new ConcurrentLinkedQueue<>();

    private final HumanResource humanResource;

    @Autowired
    public Admiral(HumanResource humanResource) {
        this.humanResource = humanResource;
        initGraph();
    }

    /** Initalize the initial graph, we using a step forward creating a graph.*/
    private void initGraph(){
        List<Troop> troops = humanResource.getUniqueServices();
        for (Troop troop : troops) {
            tryInsertTroop(troop);
        }
    }

    /** Insert batch troops, it is a transaction, which means all troop will be register or none troop will be registered.
     * Using topo sort to insert.
     * */
    public boolean tryInsertTroopBatch(Set<Troop> troops){
        int cnt = 0;
        Set<Troop> unregisteredTroops = new HashSet<>();
        for (Troop troop : troops) {
            if (!this.troops.contains(troop)) {
                unregisteredTroops.add(troop);
            }
        }

        Map<Troop, Integer> parentCounter = new HashMap<>();
        Map<Troop, Set<Troop>> childs = new HashMap<>();
        for (Troop troop: unregisteredTroops) {
            parentCounter.put(troop, 0);
            if (!childs.containsKey(troop)) { childs.put(troop, new HashSet<>()); }
            for (Troop sub: troop.getSubscribes()){
                if (!this.troops.contains(sub)) {
                    if (!childs.containsKey(sub)) {childs.put(sub, new HashSet<>());}
                    childs.get(sub).add(troop);
                    parentCounter.put(troop, parentCounter.get(troop) + 1);
                }
            }
        }

        List<Troop> topoPath = new ArrayList<>();
        for (Troop troop: unregisteredTroops) {
            if (parentCounter.get(troop) == 0) topoPath.add(troop);
        }

        while (!topoPath.isEmpty()) {
            Troop troop = topoPath.remove(0);
            cnt ++;
            for (Troop child: childs.get(troop)) {
                parentCounter.put(child, parentCounter.get(child) - 1);
                if (parentCounter.get(child) == 0) {
                    topoPath.add(child);
                }
            }
        }

        if (cnt == unregisteredTroops.size()) {
            insertTroop(unregisteredTroops);
            return true;
        }

        return false;
    }

    /** Try insert SoldierFile to admiral, if fail it will return false.*/
    public boolean tryInsertTroop(Troop troop){
        Troop[] subscribes = troop.getSubscribes();
        // already contains.
        if (this.troops.contains(troop)) {return false;}

        // if the troop is not in the events or subscribes length is zero.
        // We do not need to check if there is a loop.
        if (subscribes.length == 0){
            insertTroop(troop);
            return true;
        }

        // Only if all subscribe troops in troops can be register.
        for (Troop sub : subscribes) {
            if (!this.troops.contains(sub)) {return false;}
        }

        // Or we should first check if there has a loop while.
        // Deprecated
//        if (hasLoop(troop)){
//            String name = troop.getName();
//            log.error("SoldierFile {} registered failed because it has loop", name);
//            return false;
//        }
        insertTroop(troop);
        return true;
    }

    private void insertTroop(Set<Troop> troops){
        troops.forEach(t -> {insertTroop(t);});
    }

    private void insertTroop(Troop troop) {
        Troop[] subscribes = troop.getSubscribes();
        troops.add(troop);
        if (subscribes.length == 0) rootTroops.add(troop);
        if (!parents.containsKey(troop)) {parents.put(troop, new HashSet<>());}
        if (!childs.containsKey(troop)) {childs.put(troop, new HashSet<>());}
        for (Troop subscribe: subscribes){
            if (!childs.containsKey(subscribe)) {childs.put(subscribe, new HashSet<>());}
            Set<Troop> child = childs.get(subscribe);
            child.add(troop);
        }
    }

    /** When remove soldierFile, the graph may be change event if there have running process.*/
    public boolean tryRemoveTroop(Troop troop){
        Troop[] subscribes = troop.getSubscribes();
        if (!parents.containsKey(troop)) return false;

        for (Troop subscirbe: subscribes){
            if (!this.childs.get(subscirbe).contains(troop)) {return false;}
        }
        removeTroop(troop);
        return true;
    }

    private void removeTroop(Troop troop){
        Troop[] subscribes = troop.getSubscribes();
        troops.remove(troop);
        parents.remove(troop);
        for (Troop subscribe : subscribes){
            childs.get(subscribe).remove(troop);
        }
        if (rootTroops.contains(troop) && !idleList.contains(rootTroops.indexOf(troop)))
            idleList.add(rootTroops.indexOf(troop));
    }

    /** Find there is a loop in the graph.
     * @param troop troop object that need to assert loop.
     * */
    private boolean hasLoop(Troop troop){
        // 0 for unmeet, 1 for visiting, 2 for visited and there is no need to search again for 2.
        Map<Troop, Integer> visited = new HashMap<>();
//        for (String visitingEvent : visitingEvents){
//            visited.put(visitingEvent, 1);
//        }
        List<Pair<Troop, Integer>> path = new ArrayList<>();
        path.add(Pair.of(troop, 0));
        visited.put(troop, 1);
        while (!path.isEmpty()){
            Pair<Troop, Integer> pair = path.removeLast();
            Troop t = pair.getKey();
            int idx = pair.getValue();
            if (idx >= childs.get(t).size()){
                path.remove(pair);
                visited.put(t, 2);
                continue;
            }
            path.add(Pair.of(t, idx+1));
            Troop nextTroop = childs.get(t).toArray(new Troop[0])[idx];
            if (!visited.containsKey(nextTroop)){ visited.put(nextTroop, 0);}
            if (visited.get(nextTroop) == 1) return true;
            else if (visited.get(nextTroop) == 2){ continue;}
            visited.put(nextTroop, 1);
            path.add(Pair.of(nextTroop, 0));
        }
        return false;
    }

    /** It should called by Propaganda while a new request are going to run.*/
    public boolean createJob(String serviceName){
        return true;
    }

}
