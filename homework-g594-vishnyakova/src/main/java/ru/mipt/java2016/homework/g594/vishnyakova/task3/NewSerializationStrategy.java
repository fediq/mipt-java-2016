package ru.mipt.java2016.homework.g594.vishnyakova.task3;

/**
 * Created by Nina on 16.11.16.
 */
public abstract class NewSerializationStrategy<T> {
    abstract String serialize(T obj);
    abstract T deserialize(String s);
}
