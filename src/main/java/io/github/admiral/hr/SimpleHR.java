package io.github.admiral.hr;

import io.github.admiral.soldier.SoldierInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SimpleServiceCenter is a service center in a standalone mode.
 * It's ip and port is default to 'localhost' and '15423'
 */
public class SimpleHR implements HumanResource {

    Map<String, SimpleFile> services;

    public boolean register(SoldierInfo soldierInfo){
//        try {
//            Troop troop = new SimpleTroop(soldierInfo.getName(), soldierInfo.getSbscribes());
//            SimpleFile service = new SimpleFile(troop, "localhost", 15423);
//            services.put(soldierInfo.getName(), service);
//        } catch (Exception e) {
//            return false;
//        }
        return true;
    }

    public boolean unRegister(SoldierInfo soldierInfo){
        try {
            services.remove(soldierInfo.getName());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public List<Troop> getUniqueServices(){
        return new ArrayList<>();
    }

    public SoldierFile getService(String name){
        return services.get(name);
    }

}
