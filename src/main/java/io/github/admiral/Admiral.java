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
    /** Record troops, key is name, value is troop.*/
    private volatile Map<String, Troop> troops = new ConcurrentHashMap<>();

    /** Record event is produced by what troops. Key is event name, Value is set of troops that produce the event.*/
    private volatile Map<String, Set<Troop>> parents = new ConcurrentHashMap<>();

    /** Record event is consumed by what troops. Key is event name, Value is set of troops that consume the event.*/
    private volatile Map<String, Set<Troop>> childs = new ConcurrentHashMap<>();

    /** Record the event graph. Key is parent event name, Value is child event name.*/
    private volatile Map<String, Set<String>> events = new ConcurrentHashMap<>();

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
            tryInsertService(troop);
        }
    }

    /** Try insert SoldierFile to admiral, if fail it will return false.*/
    public boolean tryInsertService(Troop troop){
        String[] consumes = troop.getConsumes();
        String produce = troop.getProduce();
        String name = troop.getName();
        // already contains.
        if (this.troops.containsKey(name)) {return false;}
        // if the produce is not in the events or consumes length is zero.
        // We do not need to check if there is a loop.
        if (consumes.length == 0 || !events.containsKey(produce)){
            insertService(troop);
            return true;
        }
        // Or we should first check if there has a loop while.
        if (hasLoop(produce, consumes)){
            log.error("SoldierFile {} registered failed because it has loop", name);
            return false;
        }
        insertService(troop);
        return true;
    }

    private void insertService(Troop troop) {
        String[] consumes = troop.getConsumes();
        String produce = troop.getProduce();
        String name = troop.getName();
        troops.put(name, troop);
        if (consumes.length == 0) rootTroops.add(troop);
        if (!parents.containsKey(produce)) {parents.put(produce, new HashSet<>());}
        if (!childs.containsKey(produce)) {childs.put(produce, new HashSet<>());}
        if (!events.containsKey(produce)) {events.put(produce, new HashSet<>());}
        Set<Troop> parent = parents.get(produce);
        parent.add(troop);
        for (String consume: consumes){
            if (!childs.containsKey(consume)) {childs.put(consume, new HashSet<>());}
            Set<Troop> child = childs.get(consume);
            child.add(troop);
            if (!events.containsKey(consume)) {events.put(consume, new HashSet<>());}
            events.get(consume).add(produce);
        }
    }

    /** When remove soldierFile, the graph may be change event if there have running process.*/
    public boolean tryRemoveService(Troop troop){
        String[] consumes = troop.getConsumes();
        String produce = troop.getProduce();
        String name = troop.getName();
        if (!parents.get(produce).contains(troop)) {
            return false;
        }
        for (String consume: consumes){
            if (!this.events.containsKey(consume) ||
            !this.childs.get(consume).contains(troop)) {return false;}
        }
        removeService(troop);
        return true;
    }

    private void removeService(Troop troop){
        String[] consumes = troop.getConsumes();
        String produce = troop.getProduce();
        String name = troop.getName();
        troops.remove(name);
        parents.get(produce).remove(troop);
        for (String consume: consumes){
            childs.get(consume).remove(troop);
        }
        if (rootTroops.contains(troop) && !idleList.contains(rootTroops.indexOf(troop)))
            idleList.add(rootTroops.indexOf(troop));
    }

    /** Find there is a loop in the graph.
     * @param eventName the name of event that as root node.
     * @param visitingEvents visitinngEvents is a array that should not visit, the value of visitingEvents will be
     *                       seemed as visiting while begin.
     * */
    private boolean hasLoop(String eventName, String[] visitingEvents){
        // 0 for unmeet, 1 for visiting, 2 for visited and there is no need to search again for 2.
        Map<String, Integer> visited = new HashMap<>();
        for (String visitingEvent : visitingEvents){
            visited.put(visitingEvent, 1);
        }
        List<Pair<String, Integer>> path = new ArrayList<>();
        path.add(Pair.of(eventName, 0));
        visited.put(eventName, 1);
        while (!path.isEmpty()){
            Pair<String, Integer> pair = path.removeLast();
            String event = pair.getKey();
            int idx = pair.getValue();
            if (idx >= events.get(event).size()){
                path.remove(pair);
                visited.put(event, 2);
                continue;
            }
            path.add(Pair.of(event, idx+1));
            String nextEvent = events.get(event).toArray(new String[0])[idx];
            if (!visited.containsKey(nextEvent)){ visited.put(nextEvent, 0);}
            if (visited.get(nextEvent) == 1) return true;
            else if (visited.get(nextEvent) == 2){ continue;}
            visited.put(nextEvent, 1);
            path.add(Pair.of(nextEvent, 0));
        }
        return false;
    }

    /** It should called by Propaganda while a new request are going to run.*/
    public boolean createJob(String serviceName){
        return true;
    }

}
