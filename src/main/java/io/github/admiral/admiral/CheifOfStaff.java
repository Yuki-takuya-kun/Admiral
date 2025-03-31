package io.github.admiral.admiral;

import io.github.admiral.common.SignalCorp;
import io.github.admiral.service.SoldierFile;

import java.util.List;

/** Cheif of Staff is responsible to schedule what next step to take. It is the brain of the system.*/
public interface CheifOfStaff {
    /** Get next services while complete the service in param.*/
    public List<SoldierFile> nextServices(SignalCorp requestInfo);
}
