package io.github.admiral.hr;

public class RegisterException extends HRException{
    public RegisterException(String name, String msg){
        super("Error registering with name: '" + name + "': " + msg);
    }

    public RegisterException(String name, String msg, Throwable cause){
        super("Error registering with name: '" + name + "': " + msg);
    }
}
