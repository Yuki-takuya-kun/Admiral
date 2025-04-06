package io.github.admiral.hr;

import io.github.admiral.hr.standalone.SimpleHR;
import io.github.admiral.soldier.SoldierInfo;

/** Troop factory that responsible for create troop.
 * Thread unsafe.
 *
 * @author Jiahao Hwang
 * */
public class SimpleTroopFactory extends AbstractTroopFactory {

    public SimpleTroopFactory(SimpleHR simpleHR) {
        super(simpleHR);
    }

    @Override
    public SimpleTroop createTroop(SoldierInfo soldierInfo) {
        if (!(soldierInfo instanceof RemoteSoldier))
            throw new TroopCreateException(soldierInfo.getName(), "SimpleTroopFactory only supports RemoteSoldier, but got " + soldierInfo.getClass().getName());
        return new SimpleTroop(soldierInfo.getName());
    }

}
