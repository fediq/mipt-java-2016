package ru.mipt.java2016.homework.g594.sharuev.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Стратегия сериализации
 *
 * @author Fedor S. Lavrentyev
 * @since 04.10.16
 */
public interface SerializationStrategy<Value> {

    /**
     * Записать сериализованное значение в поток
     */
    void serializeToStream(Value value,
                           DataOutputStream outputStream) throws SerializationException;

    /**
     * Прочесть сериализованное значение из текущего места в потоке
     */
    Value deserializeFromStream(DataInputStream inputStream) throws SerializationException;

    /**
     * Возвращает класс, который она сериализует.
     */
    Class getSerializingClass();
}