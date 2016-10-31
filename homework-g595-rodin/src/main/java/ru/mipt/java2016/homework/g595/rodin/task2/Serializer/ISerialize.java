package ru.mipt.java2016.homework.g595.rodin.task2.Serializer;

/**
 * Created by Dmitry on 24.10.16.
 */
public interface ISerialize<ValueType> {

    String serialize(ValueType argument) throws IllegalArgumentException;

    ValueType deserialize(String argument) throws IllegalArgumentException;
}