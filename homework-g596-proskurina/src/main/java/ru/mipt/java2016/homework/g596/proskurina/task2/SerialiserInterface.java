package ru.mipt.java2016.homework.g596.proskurina.task2;

/**
 * Created by Lenovo on 31.10.2016.
 */
public interface SerialiserInterface<T> {

    String serialise(T inT);

    T deserialise(String inString);

    String getType();
}
