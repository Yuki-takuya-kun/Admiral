package io.github.admiral.admiral;

import io.github.admiral.common.RequestInfo;
import io.github.admiral.hr.BaseTroop;

/** A exception class that signify that the troop is illegal because it is a new request and the troop is not a
 * root troop.*/
public class IllegalTroopException extends RuntimeException{
    public IllegalTroopException(BaseTroop troop){
        super("troop " + troop.toString() + " is not a valid troop");
    }

    public IllegalTroopException(RequestInfo requestInfo, BaseTroop troop, String message){
        super(message.formatted(troop.toString(), requestInfo.getRequestId()) );
    }

    public IllegalTroopException(BaseTroop troop, String message){
        super(message.formatted(troop.toString()));
    }

    public IllegalTroopException(String message){
        super(message);
    }
}
