package io.github.admiral.hr;

/** Exception that indicates the troop is already created.*/
public class TroopExistException extends TroopCreateException{
    public TroopExistException(String name){
        super(name, "The troop already exists.");
    }
}
