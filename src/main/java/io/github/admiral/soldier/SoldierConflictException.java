package io.github.admiral.soldier;

/**
 * Exception that shows the soldier is conflict in a military department.
 * */
public class SoldierConflictException extends RuntimeException{

    public SoldierConflictException(){
        super();
    }

    public SoldierConflictException(String msg){
        super(msg);
    }
}
