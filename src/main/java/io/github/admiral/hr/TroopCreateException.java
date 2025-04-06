package io.github.admiral.hr;

public class TroopCreateException extends TroopException{
    public TroopCreateException(String troopName, String msg) {
        super("Error creating with name '" + troopName + "': " + msg);
    }

    public TroopCreateException(String troopName, String msg, Throwable cause) {
        super("Error creating with name '" + troopName + "': " + msg, cause);
    }
}
