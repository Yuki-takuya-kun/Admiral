package io.github.admiral.common;

import lombok.Getter;

/** A structure that conserve request information.*/
@Getter
public class RequestInfo {
    String requestId;
    Long createTime;

    public RequestInfo(String requestId, Long createTime) {
        this.requestId = requestId;
        this.createTime = createTime;
    }

    @Override
    public int hashCode(){
        return requestId.hashCode();
    }
}
