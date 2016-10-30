package ru.mipt.java2016.homework.g594.pyrkin.task2.serializer;

import java.nio.ByteBuffer;

/**
 * SerializerInterface
 * Created by randan on 10/30/16.
 */
public interface SerializerInterface<Type> {

    int sizeOfSerialize(Type object);

    ByteBuffer serialize(Type object);

    Type deserialize(ByteBuffer inputBuffer);

}
