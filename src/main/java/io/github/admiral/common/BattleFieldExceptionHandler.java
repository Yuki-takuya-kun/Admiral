package io.github.admiral.common;

import io.github.admiral.soldier.SoldierInstance;
import io.github.admiral.soldier.SoldierWrapper;

/** Exception handler interface of BattleField.*/
public interface BattleFieldExceptionHandler {

    void handle(SoldierWrapper wrapper, Throwable e);
}
