package io.github.admiral.hr;

/** A exception while it find it has a circular dependencies.
 *
 * @author Jiahao Hwang
 * */
public class TroopInCreationException extends TroopCreateException {

    public TroopInCreationException(String name) {
        super(name, "Request Troop is currently in creation, which usually means there is an circular reference");
    }
}
