package io.github.admiral.hr;

import io.github.admiral.soldier.SoldierInfo;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract class for troop factory, to create different troop.
 * */
public abstract class AbstractTroopFactory {

    /** Conserve created troops. Key is name of the troop, value troop instance.*/
    private final Map<String, BaseTroop> factory = new ConcurrentHashMap<>();

    /** Conserve created but not initialized troops.*/
    private final Map<String, BaseTroop> earlyFactory = new ConcurrentHashMap<>();

    /** Conserve in creation troops that preserve circular dependencies.*/
    private final Set<String> troopInCreation = ConcurrentHashMap.newKeySet();

    /** Human resource object that conserve all Soldier Files.*/
    private final HumanResource hr;

    public AbstractTroopFactory(HumanResource hr) {
        this.hr = hr;
    }

    /** Create troop from Soldier File. It should implement by implementation classes.
     * But the subscribes will not be injected.
     * */
    public abstract BaseTroop createTroop(SoldierInfo soldierInfo);

    /** Create troop according to its name and subscribers.*/
    public BaseTroop doCreateTroop(SoldierInfo soldierInfo){

        // if troop is already created, return.
        if (factory.containsKey(soldierInfo.getName())){
            return factory.get(soldierInfo.getName());
        }

        // if troop is in creation, which means has a circular dependencies.
        if (troopInCreation.contains(soldierInfo.getName())){
            throw new TroopInCreationException(soldierInfo.getName());
        }

        BaseTroop troop = createTroop(soldierInfo);
        List<BaseTroop> subscribes = new ArrayList<>();

        //it should iterated create the troop.
        for (String subscribe : soldierInfo.getSubscribes()){
            BaseTroop subTroop = getTroop(subscribe);
            if (subTroop != null){
                subscribes.add(subTroop);
                continue;
            }

            // recursive create
            troopInCreation.add(soldierInfo.getName());
            SoldierInfo subInfo = hr.getInfo(subscribe);
            subTroop = doCreateTroop(subInfo);
            troopInCreation.remove(soldierInfo.getName());
            subscribes.add(subTroop);
        }
        troop.setSubscribes(subscribes.toArray(new BaseTroop[subscribes.size()]));
        factory.put(soldierInfo.getName(), troop);
        return troop;
    };

    public BaseTroop getTroop(String troopName){
        return factory.get(troopName);
    }

    public List<BaseTroop> getTroops(){
        return new ArrayList<>(factory.values());
    }
}
