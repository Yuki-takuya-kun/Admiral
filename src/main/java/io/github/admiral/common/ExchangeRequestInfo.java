package io.github.admiral.common;

import lombok.Getter;

/** Request info of the exchange.*/
@Getter
public class ExchangeRequestInfo {
    private final RequestInfo requestInfo;
    private final String ip;
    private final int port;

    public ExchangeRequestInfo(final RequestInfo requestInfo, final String ip, final int port) {
        this.requestInfo = requestInfo;
        this.ip = ip;
        this.port = port;
    }
}
