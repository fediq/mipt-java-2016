package ru.mipt.java2016.homework.g597.grishutin.task2;


import java.io.IOException;
import java.io.RandomAccessFile;

public class StringSerializationStrategy implements SerializationStrategy<String> {
    private static StringSerializationStrategy instance = new StringSerializationStrategy();
    private IntegerSerializationStrategy integerSerializationStrategy = IntegerSerializationStrategy.getInstance();

    public static StringSerializationStrategy getInstance() {
        return instance;
    }

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
        raf.read(bytes);
        return new String(bytes);
    }
}
