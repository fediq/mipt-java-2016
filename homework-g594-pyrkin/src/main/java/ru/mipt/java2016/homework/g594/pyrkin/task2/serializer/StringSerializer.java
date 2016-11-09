package ru.mipt.java2016.homework.g594.pyrkin.task2.serializer;

import java.nio.ByteBuffer;

/**
 * StringSerializer
 * Created by randan on 10/30/16.
 */
public class StringSerializer implements SerializerInterface<String> {

    @Override
    public int sizeOfSerialize(String object) {
        return 2 * (object.length() + 1);
    }

    @Override
    public ByteBuffer serialize(String object) {
        ByteBuffer resultBuffer = ByteBuffer.allocate(sizeOfSerialize(object));
        for (char symbol : object.toCharArray()) {
            resultBuffer.putChar(symbol);
        }
        resultBuffer.putChar('\0');
        return resultBuffer;
    }

    @Override
    public String deserialize(ByteBuffer inputBuffer) {
        StringBuilder resultString = new StringBuilder();
        char symbol;
        while ((symbol = inputBuffer.getChar()) != '\0') {
            resultString.append(symbol);
        }
        return resultString.toString();
    }
}
