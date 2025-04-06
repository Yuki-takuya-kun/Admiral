package io.github.admiral.hr;

import java.util.Arrays;

public class InfoConflictException extends RegisterException{
    public InfoConflictException(String name, String[] subscribes1, String[] subscribes2){
        super(name, "Soldier information of '" + name + "' is already exists, which subscribes [" +
                String.join(",", subscribes1) + "], but you are going to register a soldier with same name but"
                + " subscribes [" + String.join(",", subscribes2) + "]. Please change the name of the soldier or" +
                " align soldier subscribes with registered soldier.");
    }
}
