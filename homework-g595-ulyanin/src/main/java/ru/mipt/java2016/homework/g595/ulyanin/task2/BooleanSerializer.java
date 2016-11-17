package ru.mipt.java2016.homework.g595.ulyanin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author ulyanin
 * @since 31.10.16.
 */
public class BooleanSerializer implements Serializer<Boolean> {
    private static BooleanSerializer ourInstance = new BooleanSerializer();

    public static BooleanSerializer getInstance() {
        return ourInstance;
    }

    private BooleanSerializer() { }

    @Override
    public void serialize(Boolean data, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeBoolean(data);
    }

    @Override
    public Boolean deserialize(DataInputStream dataInputStream) throws IOException {
        return dataInputStream.readBoolean();
    }
}
