package ru.mipt.java2016.homework.g594.rubanenko.task3;

import java.nio.ByteBuffer;

/**
 * Created by king on 20.11.16.
 */

public class FastStringSerializerForStudentKeyAndStudent implements FastKeyValueStorageSerializer<String> {
    @Override
    public int serializeSize(String value) {
        return 2 * (value.length() + 1);
    }

    @Override
    public ByteBuffer serializeToStream(String value) {
        ByteBuffer serialized = ByteBuffer.allocate(serializeSize(value));
        for (char c : value.toCharArray()) {
            serialized.putChar(c);
        }
        serialized.putChar('\0');
        return serialized;
    }

    @Override
    public String deserializeFromStream(ByteBuffer input) {
        StringBuilder deserialized = new StringBuilder();
        char c = input.getChar();
        while (c != '\0') {
            deserialized.append(c);
            c = input.getChar();
        }
        return deserialized.toString();
    }
}
