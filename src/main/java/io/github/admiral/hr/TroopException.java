package io.github.admiral.hr;

/**Base exception of troop.*/
public class TroopException extends RuntimeException{
    public TroopException(String msg){
        super(msg);
    }

    public TroopException(String msg, Throwable cause){
        super(msg, cause);
    }
}
