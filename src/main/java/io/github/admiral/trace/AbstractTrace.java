package io.github.admiral.trace;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.github.admiral.utils.DateTimeFormatter;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/** Abstract trace class that contains traceable messages*/
@Getter
public abstract class AbstractTrace implements Traceable{

    /** ID of the trace.*/
    protected final String traceId;

    /** Description of the trace.*/
    protected final String name;

    /** TimeStamp that record the trace.*/
    protected final Long timeStamp;

    /** Details of the trace. This can be including */
    protected final String details;

    /** Child Traceable object list.*/
    private final List<Traceable> traceables = new ArrayList<>();;

    protected AbstractTrace(final String traceId, final String name, final Long timeStamp) {
        this.traceId = traceId;
        this.name = name;
        this.timeStamp = timeStamp;
        this.details = null;
    }

    protected AbstractTrace(final String traceId, final String name, final Long timeStamp, final String details) {
        this.traceId = traceId;
        this.name = name;
        this.timeStamp = timeStamp;
        this.details = details;
    }

    protected AbstractTrace(final String traceId, final String name, final String details) {
        this.traceId = traceId;
        this.name = name;
        this.timeStamp = System.currentTimeMillis();
        this.details = details;
    }

    protected AbstractTrace(final String traceId, final String name) {
        this.traceId = traceId;
        this.name = name;
        this.timeStamp = System.currentTimeMillis();
        this.details = null;
    }

    public void addTraceable(final Traceable traceable) {
        if (traceables == null) {throw new NullPointerException("Input traceables is null");}
        traceables.add(traceable);
    }

    private final JSONObject getbaseJsonFormat() {
        JSONObject json = new JSONObject();
        json.put("traceId", traceId);
        json.put("name", name);
        json.put("time", DateTimeFormatter.formatTimeStamp(timeStamp));
        if (details != null) {
            json.put("details", details);
        }
        return json;
    }

    /** Recursive get message from current object and child traceable objects.*/
    public final String getMessage(){
        JSONObject json = getbaseJsonFormat();
        formatTrace(json);
        List<String> nextSpans = new ArrayList<>();
        for (Traceable traceable : traceables) {
            nextSpans.add(traceable.getMessage());
        }
        if (nextSpans.size() > 0) {
            json.put("nextSpans", JSON.toJSONString(nextSpans));
        }
        return json.toJSONString();
    }

    /** Only format outer trace.*/
    public final String getOuterMessage(){
        JSONObject json = getbaseJsonFormat();
        return json.toJSONString();
    }
}
