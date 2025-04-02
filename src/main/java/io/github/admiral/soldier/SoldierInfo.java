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
    private final String[] sbscribes;

    /** Pooling the soldier instance, and prevent creating same soldier.*/
    private static Map<String, SoldierInfo> soldiers = new HashMap<>();

    /**
     * Create soldierInfo, which will check if the name is registered before, if it is, it will return the
     * same instance.
     * @param name the name of soldier
     * @param subscribes
     * @return SoldierInfo
     */
    public static SoldierInfo createSoldierInfo(String name, String[] subscribes){
        if (soldiers.containsKey(name)){return soldiers.get(name);}
        SoldierInfo soldier = new SoldierInfo(name, subscribes);
        soldiers.put(name, soldier);
        return soldier;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "%s {subscribes: %s}".formatted(name, String.join(",", sbscribes));
    }
}
