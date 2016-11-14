package ru.mipt.java2016.homework.g595.ulyanin.task2;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author ulyanin
 * @since  31.10.16
 */
public class StringSerializer implements Serializer<String> {
    private static StringSerializer ourInstance = new StringSerializer();

    public static StringSerializer getInstance() {
        return ourInstance;
    }

    private StringSerializer() { }

    @Override
    public void serialize(String data, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeUTF(data);
    }

    @Override
    public String deserialize(DataInputStream dataInputStream) throws IOException {
        return dataInputStream.readUTF();
    }
}
