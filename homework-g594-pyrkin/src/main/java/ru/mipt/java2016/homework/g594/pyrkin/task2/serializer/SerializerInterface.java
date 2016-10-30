package ru.mipt.java2016.homework.g594.pyrkin.task2.serializer;

/**
 * SerializerInterface
 * Created by randan on 10/30/16.
 */
public interface SerializerInterface<Type> {

    String serialize(Type object);

    Type deserialize(String inputString);

}
