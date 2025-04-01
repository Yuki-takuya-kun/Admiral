package io.github.admiral.hr;

import io.github.admiral.soldier.Soldier;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/** Class that organize same name soldiers, all soldier should have same produces and consume.*/
@Getter
public abstract class Troop {
    protected String name;
    protected String[] consumes;
    protected String produce;

    @Override
    public boolean equals(Object o) {
        if (this == o){return true;}
        if (o == null || getClass() != o.getClass()){return false;}
        Troop troop = (Troop) o;
        return name.equals(troop.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
