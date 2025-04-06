package io.github.admiral.hr;

import lombok.Getter;

/** Class that organize same name soldiers, all soldier should have same produces and consume.*/
@Getter
public abstract class BaseTroop {

    protected final String name;
    protected BaseTroop[] subscribes;

    public BaseTroop(String name, BaseTroop[] subscribes){
        this.name = name;
        this.subscribes = subscribes;
    }

    public BaseTroop(String name){
        this.name = name;
    }

    public void setSubscribes(BaseTroop[] subscribes){
        this.subscribes = subscribes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){return true;}
        if (o == null || getClass() != o.getClass()){return false;}
        BaseTroop troop = (BaseTroop) o;
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
