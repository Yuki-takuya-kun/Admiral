package io.github.admiral.admiral;

import io.github.admiral.common.RequestInfo;

/** Exception that signify that the request is outdated. */
public class OutdatedRequestException extends RuntimeException{
    public OutdatedRequestException(RequestInfo requestInfo){
        super("request of " + requestInfo.getRequestId() + " is outdated.");
    }
}
