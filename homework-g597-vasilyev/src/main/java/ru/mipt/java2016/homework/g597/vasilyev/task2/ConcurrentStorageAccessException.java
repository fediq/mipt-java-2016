package ru.mipt.java2016.homework.g597.vasilyev.task2;

/**
 * Created by mizabrik on 31.10.16.
 */
public class ConcurrentStorageAccessException extends Exception {
    ConcurrentStorageAccessException() {
        super("Storage file is already in use.");
    }
}
