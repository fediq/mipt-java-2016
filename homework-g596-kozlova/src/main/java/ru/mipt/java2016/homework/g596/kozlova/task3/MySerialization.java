package ru.mipt.java2016.homework.g596.kozlova.task3;

public interface MySerialization<T> {
    T read(String s);

    String write(T obj);
}