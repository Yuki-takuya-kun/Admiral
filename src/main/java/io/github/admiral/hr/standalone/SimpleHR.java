package io.github.admiral.hr.standalone;

import io.github.admiral.hr.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SimpleServiceCenter is a service center in a standalone mode. Service and client is same。
 */
public class SimpleHR extends AbstractHumanResource {

    public SimpleHR(FileLoadBalancer balancer){
        super(balancer);
    }

    /** Register soldier file to human resource.*/
    public void register(SoldierFile soldierFile) {
        String name = soldierFile.getName();
        // if not been registered.
        if (!soldierInfos.containsKey(name)){
            soldierInfos.put(name, new RemoteSoldier(soldierFile.getName(), soldierFile.getSubscribes()));
            soldierFiles.put(name, new ArrayList<>());
            soldierFiles.get(name).add(soldierFile);
            return;
        }

        // check if the subscribes is same.
        String[] referArr = soldierInfos.get(name).getSubscribes();
        String[] regArr = soldierFile.getSubscribes();
        if (referArr.length != soldierFile.getSubscribes().length){
            throw new InfoConflictException(name, referArr, regArr);
        }
        Set<String> refers = Arrays.stream(soldierInfos.get(soldierFile.getName()).getSubscribes()).collect(Collectors.toSet());
        Set<String> subs = Arrays.stream(soldierFile.getSubscribes()).collect(Collectors.toSet());
        refers.retainAll(subs);
        if (refers.size() != referArr.length || refers.size() != regArr.length){
            throw new InfoConflictException(name, referArr, regArr);
        }

        // check if there has a same file
        for (SoldierFile file: soldierFiles.get(name)){
            if (file.equals(soldierFile)){
                return;
            }
        }

        // add soldier file
        soldierFiles.get(name).add(soldierFile);
    }

}
