package ru.mipt.java2016.homework.g596.egorov.task3.serializers;

/**
 * Created by евгений on 30.10.2016.
 */
public interface AdvancedSerializerInterface<T> {
    String serialize(T obj);

    T deserialize(String s);
}

