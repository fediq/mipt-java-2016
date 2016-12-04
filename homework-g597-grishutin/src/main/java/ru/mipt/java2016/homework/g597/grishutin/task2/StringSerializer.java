package ru.mipt.java2016.homework.g597.grishutin.task2;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class StringSerializer implements SerializationStrategy<String> {
    private static class SingletonHolder {
        static final StringSerializer HOLDER_INSTANCE = new StringSerializer();
    }

    public static StringSerializer getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    private final IntegerSerializer integerSerializer =
            IntegerSerializer.getInstance();
    
    @Override
    public void serialize(String value, DataOutput raf) throws IOException {
        byte[] bytes = value.getBytes();
        integerSerializer.serialize(bytes.length, raf);
        raf.write(bytes);
    }

    @Override
    public String deserialize(DataInput raf) throws IOException {
        int numBytes = integerSerializer.deserialize(raf);
        byte[] bytes = new byte[numBytes];
        raf.readFully(bytes);
        return new String(bytes);
    }

    @Override
    public Long bytesSize(String value) {
        return (long) Long.BYTES + value.getBytes().length; // string length + string itself
    }
}
