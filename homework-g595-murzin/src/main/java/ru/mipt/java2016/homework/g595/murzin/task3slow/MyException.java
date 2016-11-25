package ru.mipt.java2016.homework.g595.murzin.task3slow;

/**
 * Created by dima on 15.11.16.
 */
public class MyException extends RuntimeException {
    public MyException(String message) {
        super(message);
    }

    public MyException(String message, Throwable cause) {
        super(message, cause);
    }
}
