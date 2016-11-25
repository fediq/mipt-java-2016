package ru.mipt.java2016.homework.g594.sharuev.task3;

public class KVSException extends RuntimeException {
    public KVSException(String message) {
        super(message);
    }

    public KVSException(String message, Throwable cause) {
        super(message, cause);
    }
}
