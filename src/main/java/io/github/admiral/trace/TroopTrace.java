package io.github.admiral.trace;

import com.alibaba.fastjson2.JSONObject;
import io.github.admiral.common.RequestInfo;
import io.github.admiral.hr.BaseTroop;

/** A structure conserving how the Troop is executed.*/
public class TroopTrace implements Traceable{
    private RequestInfo requestInfo;
    private BaseTroop troop;

    @Override
    public void formatTrace(JSONObject baseObj) {

    }

    public String getMessage(){
        return "";
    }
}
