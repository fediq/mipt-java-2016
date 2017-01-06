package ru.mipt.java2016.homework.g596.fattakhetdinov.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface SerializationStrategy<T> {
    void serializeToFile(T value, DataOutput output) throws IOException;

    T deserializeFromFile(DataInput input) throws IOException;

    String getType(); //Возвращает тип стратегии сериализации
}