package ru.mipt.java2016.homework.g597.grishutin.task2;


import java.io.IOException;
import java.io.RandomAccessFile;

public class StringSerializationStrategy implements SerializationStrategy<String> {
    private static class SingletonHolder {
        static final StringSerializationStrategy HOLDER_INSTANCE = new StringSerializationStrategy();
    }

    public static StringSerializationStrategy getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    private final IntegerSerializationStrategy integerSerializationStrategy =
            IntegerSerializationStrategy.getInstance();
    
    @Override
    public void serialize(String value, RandomAccessFile raf) throws IOException {
        byte[] bytes = value.getBytes();
        integerSerializationStrategy.serialize(bytes.length, raf);
        raf.write(bytes);
    }

    @Override
    public String deserialize(RandomAccessFile raf) throws IOException {
        int numBytes = integerSerializationStrategy.deserialize(raf);
        byte[] bytes = new byte[numBytes];
        raf.readFully(bytes);
        return new String(bytes);
    }

    @Override
    public Long bytesSize(String value) {
        return (long) 4 + value.getBytes().length; // string length + string itself
    }
}
