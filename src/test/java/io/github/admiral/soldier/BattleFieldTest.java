package io.github.admiral.soldier;

import io.github.admiral.common.SimpleTaskInfo;
import io.github.admiral.common.TaskInfo;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class BattleFieldTest {

    BattleField bf;

    public class SubmittedTask{
        public String run(){
            return "hello world.";
        }
    }

    @Test
    void testSubmit() {
        bf = new BattleField(1, 1, 1000, 1, 1000, null);
        SubmittedTask s = new SubmittedTask();
        Method method = null;
        try {
            method = s.getClass().getMethod("run");
            String res = (String) method.invoke(s);
            assertEquals("hello world.", res);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        assertNotNull(method);

        LocalSoldier soldierInfo = new LocalSoldier("hi", new String[]{"None"});
        TaskInfo taskInfo = new SimpleTaskInfo("id1");
        SoldierInstance soldier = new SoldierInstance(soldierInfo, s, method);
        SoldierWrapper soldierWrapper = new SoldierWrapper(taskInfo, soldier);
        bf.submit(soldierWrapper);
    }
}