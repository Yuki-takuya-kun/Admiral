package io.github.admiral.common;

import lombok.Getter;

@Getter
public class RequestInfo {
    String requestId;

    @Override
    public int hashCode(){
        return requestId.hashCode();
    }
}
