package io.github.admiral.hr;

public class SimpleTroop extends Troop {

    public SimpleTroop(String name,
                       Troop[] subscribes) {
        this.name = name;
        this.subscribes = subscribes;
    }
}
