package io.github.admiral.hr;

import io.github.admiral.Admiral;
import io.github.admiral.soldier.SoldierInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ServiceCenter interface. ServiceCenter is a middleware that can helps {@link Admiral} to choose next soldier to
 * execute the task.
 */
public interface HumanResource {

    /** Register soldier to service center.*/
    void register(SoldierFile soldierFile);

    /** Get soldier information. */
    SoldierInfo getInfo(String name);

    /** Get all soldier files given name.*/
    List<SoldierFile> getFiles(String name);

    /** Get a soldier file given name. That can use load balanced algorithm to complete it.*/
    SoldierFile getFile(String name);
}
