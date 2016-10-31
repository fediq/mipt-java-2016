package ru.mipt.java2016.homework.g596.egorov.task2;

import ru.mipt.java2016.homework.g596.egorov.task2.serializers.SerializerInterface;

import java.io.DataInputStream;

import java.io.DataOutputStream;

import java.io.IOException;

/**
 * Created by евгений on 30.10.2016.
 */
public class SerializerofString implements SerializerInterface<String> {
    @Override
    public String deserialize(DataInputStream rd) throws IOException {
        return rd.readUTF();
    }

    @Override
    public void serialize(DataOutputStream wr, String obj) throws IOException {
        wr.writeUTF(obj);
    }
}
