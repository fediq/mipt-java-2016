package ru.mipt.java2016.homework.g595.manucharyan.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Vardan Manucharyan
 * @since 30.10.16
 */
public class ConcreteStrategyString implements SerializationStrategy<String> {
    @Override
    public void serializeToStream(String value, DataOutputStream stream) throws IOException {
        writeString(stream, value);
    }

    @Override
    public String deserializeFromStream(DataInputStream stream) throws IOException {
        return readString(stream);
    }

    //serializing functions
    public static void writeString(DataOutputStream stream, String string) throws IOException {
        stream.writeInt(string.length());
        stream.write(string.getBytes("UTF-8"));
    }

    public static String readString(DataInputStream stream) throws IOException {
        int len = stream.readInt();
        byte[] bytes = new byte[len];
        stream.read(bytes);
        return new String(bytes);
    }
}
