package io.github.admiral.soldier;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SoldierFactory {

    /** Pooling the soldier instance, and prevent creating same soldier.*/
    private static Map<String, LocalSoldier> soldierInfos = new HashMap<>();

    /** Conserve all soldiers that created.*/
    private static Map<SoldierInfo, SoldierInstance> soldierInstances = new HashMap<>();

    /**
     * Create LocalSoldier, which will check if the name is registered before, if it is, it will return the
     * same instance.
     * @param name the name of soldier
     * @param subscribes
     * @return LocalSolider
     */
    public static SoldierInfo createSoldierInfo(String name, String[] subscribes){
        // in current version, we do not support same name in a single process.
        if (soldierInfos.containsKey(name)){
            throw new SoldierConflictException("SoldierInfo [%s] is already created.".formatted(name));
        }
        LocalSoldier soldier = new LocalSoldier(name, subscribes);
        soldierInfos.put(name, soldier);
        return soldier;
    }

    /**Create soldier instance.*/
    public static SoldierInstance createSoldierInstance(SoldierInfo soldierInfo, Object bean, Method method){
        if (soldierInstances.containsKey(soldierInfo)){
            throw new SoldierConflictException("SoldierInstance [%s] is already created.".formatted(soldierInfo.getName()));
        }
        SoldierInstance soldierInstance = new SoldierInstance(soldierInfo, bean, method);
        soldierInstances.put(soldierInfo, soldierInstance);
        return soldierInstance;
    }
}
