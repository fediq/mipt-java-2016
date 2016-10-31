package ru.mipt.java2016.homework.g596.fattakhetdinov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface SerializationStrategy<T> {
    void serializeToFile(T value, DataOutputStream output) throws IOException;

    T deserializeFromFile(DataInputStream input) throws IOException;

    String getType(); //Возвращает тип стратегии сериализации
}
