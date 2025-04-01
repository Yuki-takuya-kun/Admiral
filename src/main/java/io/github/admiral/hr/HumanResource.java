package io.github.admiral.hr;

import io.github.admiral.Admiral;
import io.github.admiral.soldier.SoldierInfo;

import java.util.List;

/**
 * ServiceCenter interface. ServiceCenter is a middleware that can helps {@link Admiral} to choose next soldier to
 * execute the task.
 */
public interface HumanResource {
    /** Register soldier to service center.*/
    boolean register(SoldierInfo soldierInfo);

    /** Remove soldier from service center.*/
    boolean unRegister(SoldierInfo soldierInfo);

    /** Get all services in service center. It should responsible for discard same name services.*/
    List<Troop> getUniqueServices();

    /** Get an usuable service given service name, you can use load balance in this method. */
    SoldierFile getService(String name);
}
