package io.github.admiral.admiral;

import io.github.admiral.common.RequestInfo;
import io.github.admiral.hr.BaseTroop;

import java.util.List;

/** Cheif of Staff is responsible to schedule what next step to take. It is the brain of the system.*/
public interface ChiefOfStaff {

    /** Assert if the request is complete.*/
    public boolean isEnd(RequestInfo requestInfo);

    /** Get next services while complete the service in param.*/
    public List<BaseTroop> nextTroop(RequestInfo requestInfo, BaseTroop troop);
}
