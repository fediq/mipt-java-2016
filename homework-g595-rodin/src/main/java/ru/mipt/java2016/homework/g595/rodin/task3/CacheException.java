package ru.mipt.java2016.homework.g595.rodin.task3;

/**
 * Created by Dmitry on 17.11.16.
 */
public class CacheException extends Exception {
    public CacheException(String message) {
        super(message);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }
}