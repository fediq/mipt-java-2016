package ru.mipt.java2016.homework.g594.stepanov.task3;

public abstract class ObjectSerialisator<T> {

    public abstract String toString(T object);

    public abstract T toObject(String s);
}
