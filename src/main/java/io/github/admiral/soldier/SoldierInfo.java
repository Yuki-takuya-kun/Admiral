package io.github.admiral.soldier;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Reserve soldier instance base information.
 *
 * @author Jiahao Hwang
 */
@Getter
@RequiredArgsConstructor(access= AccessLevel.PRIVATE)
public class SoldierInfo {
    private final String name;
    private final String[] subscribes;
    private final String[] produces;

    /** Pooling the soldier instance, and prevent creating same soldier.*/
    private static Map<String, SoldierInfo> soldiers = new HashMap<>();

    /**
     * Create soldierInfo, which will check if the name is registered before, if it is, it will return the
     * same instance.
     * @param name the fully qualified class name of the soldier, which compose of the barrack name and the method name.
     * @param subscribes
     * @param produces
     * @return SoldierInfo
     */
    public static SoldierInfo createSoldierInfo(String name, String[] subscribes, String[] produces){
        if (soldiers.containsKey(name)){return soldiers.get(name);}
        SoldierInfo soldier = new SoldierInfo(name, subscribes, produces);
        soldiers.put(name, soldier);
        return soldier;
    }
}
