package io.github.admiral.hr;

/**
 * Exception that shows that the file is conflict, which means two file has same name, same
 * */
public class FileConflictException extends RegisterException{
    public FileConflictException(SoldierFile soldierFile) {
        super(soldierFile.getName(), "Soldier with name: '" + soldierFile.getName() +
                "' at host: '" + soldierFile.getPort()
                + "' and port: '" + soldierFile.getPort()
                + "' is already registered. Please do not register again.");
    }
}
