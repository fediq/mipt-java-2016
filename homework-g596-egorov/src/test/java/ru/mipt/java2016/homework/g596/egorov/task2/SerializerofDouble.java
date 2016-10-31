package ru.mipt.java2016.homework.g596.egorov.task2;

import ru.mipt.java2016.homework.g596.egorov.task2.serializers.SerializerInterface;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by евгений on 30.10.2016.
 */
public class SerializerofDouble implements SerializerInterface<Double> {

    @Override
    public Double deserialize(DataInputStream rd) throws IOException {
        return rd.readDouble();
    }

    @Override
    public void serialize(DataOutputStream wr, Double obj) throws IOException {
        wr.writeDouble(obj);
    }
}
