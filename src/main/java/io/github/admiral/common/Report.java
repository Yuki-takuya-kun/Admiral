package io.github.admiral.common;

/** Data class that report the soldier finish status.
 *
 * @author Jiahao Hwang
 * */
public class Report {
    Result result;

    /** Fail reason if result is fail.*/
    String reason;

    /** Details that should report to headquarter.*/
    String details;
}
