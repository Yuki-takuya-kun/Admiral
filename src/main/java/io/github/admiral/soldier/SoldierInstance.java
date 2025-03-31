package io.github.admiral.soldier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiredArgsConstructor
@Slf4j(topic= "SoldierLogger")
public class SoldierInstance {
    private final SoldierInfo soldierInfo;
    private final Object soldierObject;
    private final Method executeMethod;

    public Object execute(Object... args) {
        try {
            return executeMethod.invoke(soldierObject, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("While execute {}.{}, some error occur:\n", soldierObject.getClass().getName(), executeMethod.getName(), e);
        }
        return null;
    }

}

