package io.github.admiral.hr;

/** Exception classes that belongs to human resource process.*/
public class HRException extends RuntimeException {
    public HRException(String msg) {
        super(msg);
    }

    public HRException(String name, String msg, Throwable cause) {
        super(msg, cause);
    }
}
