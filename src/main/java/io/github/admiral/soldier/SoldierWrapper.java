package io.github.admiral.soldier;

import io.github.admiral.common.TaskInfo;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** Wrap soldier with invocable functions. It will inject call back functions.
 * Such as complete call backs and push message to spokesman.*/
public class SoldierWrapper implements Supplier<Object> {

    /** Soldier needed to invoke.*/
    private final SoldierInstance soldier;

    /** Arguments that needs to input to soldier instance.*/
    private final Object[] args;

    /** On call functions.*/
    private List<Pair<Object, Method>> onCalls;

    /** Callback functions.*/
    private List<Pair<Object, Method>> callbacks;

    /** Task information*/
    private TaskInfo taskInfo;

    public SoldierWrapper(TaskInfo taskInfo,
                          SoldierInstance soldier,
                          Object[] args,
                          List<Pair<Object, Method>> onCalls,
                          List<Pair<Object, Method>> callbacks) {
        this.taskInfo = taskInfo;
        this.soldier = soldier;
        this.args = args;
        this.onCalls = onCalls;
        this.callbacks = callbacks;
    }

    public SoldierWrapper(TaskInfo taskInfo, SoldierInstance soldier, Object[] args) {
        this(taskInfo, soldier, args, new ArrayList<>(), new ArrayList<>());
    }

    public SoldierWrapper(TaskInfo taskInfo, SoldierInstance soldier) {
        this(taskInfo, soldier, null, new ArrayList<>(), new ArrayList<>());
    }

    /** Add on call function, the method argument.*/
    public void addOnCall(Pair<Object, Method> onCall) {
        onCalls.add(onCall);
    }

    public void addCallback(Pair<Object, Method> callback) {
        callbacks.add(callback);
    }

    public TaskInfo getTaskInfo() {
        return taskInfo;
    }

    public Object get(){
        Object[] args = this.args;

        onCalls.forEach(onCall -> {
            Object caller = onCall.getLeft();
            Method method = onCall.getRight();
            try {
                method.invoke(caller, taskInfo, new Object[]{args});
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });

        Object result;
        if (args == null){
            result = soldier.execute();
        } else {
            result = soldier.execute(args);
        }

        callbacks.forEach(callback ->{
            Object caller = callback.getLeft();
            Method method = callback.getRight();
            try {
                method.invoke(caller, taskInfo, result);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });

        return result;
    }

}
