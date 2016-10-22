package ru.mipt.java2016.homework.g595.romanenko.task2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
* Стратегия сериализации
*
* @author Fedor S. Lavrentyev
* @since 04.10.16
**/

interface SerializationStrategy<Value> {

    /**
     * Вернуть сериализованное значение в виде массива байт
     */
    byte[] serializeToBytes(Value value);

    /**1
     * Записать сериализованное значение в поток
     */
    void serializeToStream(Value value, OutputStream outputStream) throws IOException;

    /**
     * Прочесть сериализованное значение из текущего места в потоке
     */
    Value deserializeFromStream(InputStream inputStream) throws IOException;

    /**
     * Прочесть сериализованное значение из массива байт, начиная с нулевого байта
     */
    default Value deserialize(byte[] bytes) {
        return deserialize(bytes, 0);
    }

    /**
     * Прочесть сериализованное значение из массива байт, начиная с указанного смещения
     */
    Value deserialize(byte[] bytes, int offset);
}