package io.github.admiral.department;

import java.io.Serializable;
import java.util.Map;

/** Execute result of soldier.*/
public class Result implements Serializable {
    protected final String requestId;
    protected final String taskName;
    /** Result mapping, key is task name, value is the result.*/
    protected final Object result;

    public Result(String requestId, String taskName, Object result) {
        this.requestId = requestId;
        this.taskName = taskName;
        this.result = result;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getTaskName() {
        return taskName;
    }
}
