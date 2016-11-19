package ru.mipt.java2016.homework.g594.krokhalev.task3;

public interface SerializationStrategy<T> {
    T deserialize (byte[] buff);
    byte[] serialize (T value);
}
