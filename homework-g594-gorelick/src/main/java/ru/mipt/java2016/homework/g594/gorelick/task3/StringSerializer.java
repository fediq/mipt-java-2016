package ru.mipt.java2016.homework.g594.gorelick.task2;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by alex on 10/31/16.
 */
public class StringSerializer implements Serializer<String> {
    @Override
    public ByteBuffer serialize(String object) throws IOException {
        ByteBuffer bytes = ByteBuffer.allocate(2*object.length() + 2);
        for(char c: object.toCharArray())
            bytes.putChar(c);
        bytes.putChar('\0');
        return bytes;
    }

    @Override
    public String deserialize(ByteBuffer array) throws IOException {
        StringBuilder new_string = new StringBuilder();
        char current = array.getChar();
        while (current != '\0') {
            new_string.append(current);
            current = array.getChar();
        }
        return  new_string.toString();
    }
}
