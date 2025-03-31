package io.github.admiral.communicate;

/** A enum class that includes signals from military to admiral.
 *
 * @author Jiahao Hwang
 * */
public enum Report{
    /** Report execute result of the execute command. Only support from military department to admiral.*/
    Report,

    /** Register needed event that should push to browser. Only support from military department having spokesman.*/
    Register,
}
