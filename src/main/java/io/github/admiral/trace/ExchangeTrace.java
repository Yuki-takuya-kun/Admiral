package io.github.admiral.trace;

import com.alibaba.fastjson2.JSONObject;
import io.github.admiral.common.ExchangeRequestInfo;
import io.github.admiral.utils.DateTimeFormatter;
import io.github.admiral.utils.TraceFormatter;
import lombok.Getter;

import java.util.Date;

/** Exchange Trace that record the result of exchange.*/
@Getter
public class ExchangeTrace extends AbstractTrace{
    private final ExchangeState state;
    private final ExchangeRequestInfo requestInfo;

    public ExchangeTrace(final ExchangeRequestInfo requestInfo,
                         final String name, final Long timeStamp, final ExchangeState state) {
        super(requestInfo.getRequestInfo().getRequestId(), name, timeStamp);
        this.state = state;
        this.requestInfo = requestInfo;
    }

    public ExchangeTrace(final ExchangeRequestInfo requestInfo,
                         final String name, final Long timeStamp, final ExchangeState state, final String details){
        super(requestInfo.getRequestInfo().getRequestId(), name, timeStamp, details);
        this.state = state;
        this.requestInfo = requestInfo;
    }

    public void formatTrace(final JSONObject json){
        json.put("requestIP", requestInfo.getIp());
        json.put("requestPort", requestInfo.getPort());
    }
}
