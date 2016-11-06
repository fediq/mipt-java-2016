package ru.mipt.java2016.homework.g596.gerasimov.task2.Serializer;

import java.nio.ByteBuffer;

/**
 * Created by geras-artem on 30.10.16.
 */
public class StringSerializer implements ISerializer<String> {
    @Override
    public int sizeOfSerialization(String object) {
        return 2 * (object.length() + 1);
    }

    @Override
    public ByteBuffer serialize(String object) {
        ByteBuffer result = ByteBuffer.allocate(2 * (object.length() + 1));
        for (char c : object.toCharArray()) {
            result.putChar(c);
        }
        result.putChar('\0');
        return result;
    }

    @Override
    public String deserialize(ByteBuffer code) {
        StringBuilder result = new StringBuilder();
        while (true) {
            char current;
            current = code.getChar();
            if (current == '\0') {
                break;
            }
            result.append(current);
        }
        return result.toString();
    }
}
