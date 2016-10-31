package ru.mipt.java2016.homework.g596.egorov.task2;

import ru.mipt.java2016.homework.g596.egorov.task2.serializers.SerializerInterface;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by евгений on 30.10.2016.
 */
public class SerializerofInteger implements SerializerInterface<Integer> {
    @Override
    public Integer deserialize(DataInputStream rd) throws IOException {
        return rd.readInt();
    }

    @Override
    public void serialize(DataOutputStream wr, Integer obj) throws IOException {
        wr.writeInt(obj);
    }
}
