package io.github.admiral.hr;

import lombok.Getter;

/** Class that organize same name soldiers, all soldier should have same produces and consume.*/
@Getter
public abstract class Troop {
    protected String name;
    protected Troop[] subscribes;

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

    @Override
    public String toString() {
        return name;
    }
}
