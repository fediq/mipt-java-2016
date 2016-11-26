package ru.mipt.java2016.homework.base.task2;

/**
 * @author Fedor S. Lavrentyev
 * @since 01.11.16
 */
public class MalformedDataException extends RuntimeException {
    public MalformedDataException() {
    }

    public MalformedDataException(String message) {
        super(message);
    }

    public MalformedDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedDataException(Throwable cause) {
        super(cause);
    }
}
