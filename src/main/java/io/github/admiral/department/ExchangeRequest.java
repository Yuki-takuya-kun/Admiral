package io.github.admiral.department;

/** Request information of exchanging. Including request id. */
public abstract class ExchangeRequest {
    /** Request id for the request, see {@link io.github.admiral.common.RequestInfo}*/
    private final String requestId;
    private final String taskName;

    public ExchangeRequest(String requestId,
                           String taskName) {
        this.requestId = requestId;
        this.taskName = taskName;
    }
}
