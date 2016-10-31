package ru.mipt.java2016.homework.g596.egorov.task2;

import ru.mipt.java2016.homework.g596.egorov.task2.serializers.SerializerInterface;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by евгений on 30.10.2016.
 */
public class SerializerofStudentKey implements SerializerInterface<StudentKey> {
    @Override
    public void serialize(DataOutputStream wr, StudentKey obj) throws IOException {
        wr.writeInt(obj.getGroupId());
        wr.writeUTF(obj.getName());
    }

    @Override
    public StudentKey deserialize(DataInputStream rd) throws IOException {
        int grId = rd.readInt();
        String stName = rd.readUTF();
        return new StudentKey(grId, stName);
    }
}

