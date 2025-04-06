package io.github.admiral;

import io.github.admiral.hr.HumanResource;
import io.github.admiral.hr.BaseTroop;
import io.github.admiral.hr.PersonnelMinistry;
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
    private volatile Set<BaseTroop> troops = ConcurrentHashMap.newKeySet();

    /** Record event is produced by what troops. Key is troop, Value is set of troops that name the event.*/
    private volatile Map<BaseTroop, Set<BaseTroop>> parents = new ConcurrentHashMap<>();

    /** Record event is consumed by what troops. Key is troop, Value is set of troops that consume the event.*/
    private volatile Map<BaseTroop, Set<BaseTroop>> childs = new ConcurrentHashMap<>();

    /** Record root troops. Each root soldierFile will maintain a message queue that can poll message for each completed
     * task. The index of rootService is the partition index in message queue.*/
    private volatile List<BaseTroop> rootTroops = new CopyOnWriteArrayList<>();

    /** Record removed troops that incur any partition is idle, reuse them.*/
    private volatile Queue<Integer> idleList = new ConcurrentLinkedQueue<>();

    private final PersonnelMinistry personnelMinistry;

    @Autowired
    public Admiral(PersonnelMinistry personnelMinistry) {
        this.personnelMinistry = personnelMinistry;
        initGraph();
    }

    /** Initalize the initial graph, we using a step forward creating a graph.*/
    private void initGraph(){
        List<BaseTroop> troops = personnelMinistry.getAllTroops();
        for (BaseTroop troop : troops) {
            tryInsertTroop(troop);
        }
    }

    /** Insert batch troops, it is a transaction, which means all troop will be register or none troop will be registered.
     * Using topo sort to insert.
     * */
    public boolean tryInsertTroopBatch(Set<BaseTroop> troops){
        int cnt = 0;
        Set<BaseTroop> unregisteredTroops = new HashSet<>();
        for (BaseTroop troop : troops) {
            if (!this.troops.contains(troop)) {
                unregisteredTroops.add(troop);
            }
        }

        Map<BaseTroop, Integer> parentCounter = new HashMap<>();
        Map<BaseTroop, Set<BaseTroop>> childs = new HashMap<>();
        for (BaseTroop troop: unregisteredTroops) {
            parentCounter.put(troop, 0);
            if (!childs.containsKey(troop)) { childs.put(troop, new HashSet<>()); }
            for (BaseTroop sub: troop.getSubscribes()){
                if (!this.troops.contains(sub)) {
                    if (!childs.containsKey(sub)) {childs.put(sub, new HashSet<>());}
                    childs.get(sub).add(troop);
                    parentCounter.put(troop, parentCounter.get(troop) + 1);
                }
            }
        }

        List<BaseTroop> topoPath = new ArrayList<>();
        for (BaseTroop troop: unregisteredTroops) {
            if (parentCounter.get(troop) == 0) topoPath.add(troop);
        }

        while (!topoPath.isEmpty()) {
            BaseTroop troop = topoPath.remove(0);
            cnt ++;
            for (BaseTroop child: childs.get(troop)) {
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
    public boolean tryInsertTroop(BaseTroop troop){
        BaseTroop[] subscribes = troop.getSubscribes();
        // already contains.
        if (this.troops.contains(troop)) {return false;}

        // if the troop is not in the events or subscribes length is zero.
        // We do not need to check if there is a loop.
        if (subscribes.length == 0){
            insertTroop(troop);
            return true;
        }

        // Only if all subscribe troops in troops can be register.
        for (BaseTroop sub : subscribes) {
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

    private void insertTroop(Set<BaseTroop> troops){
        troops.forEach(t -> {insertTroop(t);});
    }

    private void insertTroop(BaseTroop troop) {
        BaseTroop[] subscribes = troop.getSubscribes();
        troops.add(troop);
        if (subscribes.length == 0) rootTroops.add(troop);
        if (!parents.containsKey(troop)) {parents.put(troop, new HashSet<>());}
        if (!childs.containsKey(troop)) {childs.put(troop, new HashSet<>());}
        for (BaseTroop subscribe: subscribes){
            if (!childs.containsKey(subscribe)) {childs.put(subscribe, new HashSet<>());}
            Set<BaseTroop> child = childs.get(subscribe);
            child.add(troop);
        }
    }

    /** When remove soldierFile, the graph may be change event if there have running process.*/
    public boolean tryRemoveTroop(BaseTroop troop){
        BaseTroop[] subscribes = troop.getSubscribes();
        if (!parents.containsKey(troop)) return false;

        for (BaseTroop subscirbe: subscribes){
            if (!this.childs.get(subscirbe).contains(troop)) {return false;}
        }
        removeTroop(troop);
        return true;
    }

    private void removeTroop(BaseTroop troop){
        BaseTroop[] subscribes = troop.getSubscribes();
        troops.remove(troop);
        parents.remove(troop);
        for (BaseTroop subscribe : subscribes){
            childs.get(subscribe).remove(troop);
        }
        if (rootTroops.contains(troop) && !idleList.contains(rootTroops.indexOf(troop)))
            idleList.add(rootTroops.indexOf(troop));
    }

    /** Find there is a loop in the graph.
     * @param troop troop object that need to assert loop.
     * */
    private boolean hasLoop(BaseTroop troop){
        // 0 for unmeet, 1 for visiting, 2 for visited and there is no need to search again for 2.
        Map<BaseTroop, Integer> visited = new HashMap<>();
//        for (String visitingEvent : visitingEvents){
//            visited.put(visitingEvent, 1);
//        }
        List<Pair<BaseTroop, Integer>> path = new ArrayList<>();
        path.add(Pair.of(troop, 0));
        visited.put(troop, 1);
        while (!path.isEmpty()){
            Pair<BaseTroop, Integer> pair = path.removeLast();
            BaseTroop t = pair.getKey();
            int idx = pair.getValue();
            if (idx >= childs.get(t).size()){
                path.remove(pair);
                visited.put(t, 2);
                continue;
            }
            path.add(Pair.of(t, idx+1));
            BaseTroop nextTroop = childs.get(t).toArray(new BaseTroop[0])[idx];
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
