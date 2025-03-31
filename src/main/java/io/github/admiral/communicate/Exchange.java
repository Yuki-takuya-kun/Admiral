package io.github.admiral.communicate;

/** A enum class that includes signals between military departments.
 *
 * @author Jiahao Hwang
 * */
public enum Exchange {
    /** Request data from other military department.*/
    Request,

    /** Return data to other military department that send the request signal.*/
    Return,
}
