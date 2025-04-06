package io.github.admiral.hr;

import io.github.admiral.soldier.SoldierInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract class for human resource.
 * */
public abstract class AbstractHumanResource implements HumanResource {

    /** Conserving soldier infos without details such as host and ip.*/
    protected final Map<String, SoldierInfo> soldierInfos = new ConcurrentHashMap<>();

    protected final Map<String, List<SoldierFile>> soldierFiles = new ConcurrentHashMap<>();

    /** Soldier cache for creating transaction.*/
    protected final Map<String, List<SoldierFile>> soldierFileCache = new ConcurrentHashMap<>();

    protected FileLoadBalancer balancer;

    protected AbstractHumanResource(FileLoadBalancer balancer) {
        this.balancer = balancer;
    }

    public List<SoldierInfo> getInfos(String name){ return soldierInfos.values().stream().toList();}

    public SoldierInfo getInfo(String name) {
        return soldierInfos.get(name);
    }

    public List<SoldierFile> getFiles(String name) {
        return soldierFiles.get(name);
    }

    public SoldierFile getFile(String name){
        return balancer.getFileBalanced(getFiles(name));
    };
}
