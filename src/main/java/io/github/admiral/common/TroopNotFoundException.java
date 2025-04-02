package io.github.admiral.common;

import io.github.admiral.hr.Troop;

/**
 * This exception is going to signify that the troop is not in a collection.
 * Which is used by {@link io.github.admiral.admiral.ChiefOfStaff}
 * */
public class TroopNotFoundException extends Exception {
    public TroopNotFoundException(Troop troop) {
        super(troop.toString() + " not found");
    }

    public TroopNotFoundException(Troop troop, String message) {
        super(troop.toString() + " not found: " + message);
    }
}
