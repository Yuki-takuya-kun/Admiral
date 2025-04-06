package io.github.admiral.trace;

import io.github.admiral.hr.BaseTroop;

/**
 * This exception is going to signify that the troop is not in a collection.
 * Which is used by {@link io.github.admiral.admiral.ChiefOfStaff}
 * */
public class TroopNotFoundException extends RuntimeException {
    public TroopNotFoundException(BaseTroop troop) {
        super(troop.toString() + " not found");
    }

    public TroopNotFoundException(BaseTroop troop, String message) {
        super(troop.toString() + " not found: " + message);
    }
}
