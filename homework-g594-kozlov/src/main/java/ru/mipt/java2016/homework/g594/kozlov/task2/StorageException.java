package ru.mipt.java2016.homework.g594.kozlov.task2;

/**
 * Created by Anatoly on 25.10.2016.
 */
public class StorageException extends Exception {
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
