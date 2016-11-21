package ru.mipt.java2016.homework.g595.manucharyan.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Vardan Manucharyan
 * @since 30.10.16
 */
public class ConcreteStrategyStringRandomAccess implements SerializationStrategyRandomAccess<String> {
    @Override
    public void serializeToFile(String value, RandomAccessFile output) throws IOException {
        writeString(output, value);
    }

    @Override
    public String deserializeFromFile(RandomAccessFile input) throws IOException {
        return readString(input);
    }

    //serializing functions
    public static void writeString(RandomAccessFile output, String string) throws IOException {
        output.writeInt(string.length());
        output.write(string.getBytes("UTF-8"));
    }

    public static String readString(RandomAccessFile input) throws IOException {
        int len = input.readInt();
        byte[] bytes = new byte[len];
        input.read(bytes);
        return new String(bytes);
    }
}
