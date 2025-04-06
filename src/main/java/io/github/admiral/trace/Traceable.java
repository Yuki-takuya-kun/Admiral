package io.github.admiral.trace;

import com.alibaba.fastjson2.JSONObject;

/** Interface that signifying the structure that conserve the traceable message.
 * Such as which soldier the chief of staff schedule.
 * When military department fetch the data from other military department. etc.
 *
 * @author Jiahao Hwang
 * */
public interface Traceable {

    /** Each structure much implement format method that conserve the necessary message.*/
    void formatTrace(final JSONObject baseObj);

    /** Format whole message that includes children traces.*/
    String getMessage();
}
