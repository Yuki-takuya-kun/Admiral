package io.github.admiral.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/** Format time using system.currentTimeMillis*/
public class DateTimeFormatter {
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String formatTimeStamp(Long timeStamp) {
        if (timeStamp == null) throw new IllegalArgumentException("Input time stamp should not be None.");
        Date date = new Date(timeStamp);
        return sdf.format(date);
    }
}
