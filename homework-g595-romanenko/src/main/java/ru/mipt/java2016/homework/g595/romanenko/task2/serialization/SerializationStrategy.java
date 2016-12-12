package ru.mipt.java2016.homework.g595.romanenko.task2.serialization;


import java.io.*;

/**
 * Стратегия сериализации
 *
 * @author Fedor S. Lavrentyev
 * @since 04.10.16
 **/

public interface SerializationStrategy<Value> {

    /**
     * Вернуть сериализованное значение в виде массива байт
     */
    default byte[] serializeToBytes(Value value) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        serializeToStream(value, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Записать сериализованное значение в поток
     */
    void serializeToStream(Value value, OutputStream outputStream) throws IOException;

    /**
     * Получить размер в байтах после сериализации
     */
    int getBytesSize(Value value);

    /**
     * Прочесть сериализованное значение из текущего места в потоке
     */
    Value deserializeFromStream(InputStream inputStream) throws IOException;

    /**
     * Прочесть сериализованное значение из массива байт, начиная с нулевого байта
     */
    default Value deserialize(byte[] bytes) throws IOException {
        return deserialize(bytes, 0);
    }

    /**
     * Прочесть сериализованное значение из массива байт, начиная с указанного смещения
     */
    default Value deserialize(byte[] bytes, int offset) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes, offset, bytes.length - offset);
        return deserializeFromStream(byteArrayInputStream);
    }

    /**
     * Прочесть сериализованное значение из текущего места в потоке без дессериализации
     *
     * @return
     */
    default byte[] readValueAsBytes(InputStream inputStream) throws IOException {
        Integer totalSize = IntegerSerializer.getInstance().deserializeFromStream(inputStream);
        byte[] bytes = new byte[totalSize];
        Integer length = totalSize - IntegerSerializer.getInstance().cntBYTES;
        inputStream.read(bytes, IntegerSerializer.getInstance().cntBYTES, length);
        byte[] lenBytes = IntegerSerializer.getInstance().serializeToBytes(totalSize);
        for (int i = 0; i < IntegerSerializer.getInstance().cntBYTES; i++) {
            bytes[i] = lenBytes[i];
        }
        return bytes;
    }
}
