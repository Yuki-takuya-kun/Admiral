package io.github.admiral.soldier;

import lombok.Getter;

/**
 * Reserve soldier instance base information.
 *
 * @author Jiahao Hwang
 */
@Getter
public abstract class SoldierInfo {
    private final String name;
    private final String[] subscribes;

    protected SoldierInfo(String name, String[] subscribes) {
        this.name = name;
        this.subscribes = subscribes;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "%s {subscribes: %s}".formatted(name, String.join(",", subscribes));
    }
}
