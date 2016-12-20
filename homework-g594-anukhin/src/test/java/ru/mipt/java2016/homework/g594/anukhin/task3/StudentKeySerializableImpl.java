package ru.mipt.java2016.homework.g594.anukhin.task3;

import ru.mipt.java2016.homework.g594.anukhin.task3.Serializable;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by clumpytuna on 29.10.16.
 */
public class StudentKeySerializableImpl implements Serializable<StudentKey> {
    @Override
    public void serialize(DataOutputStream output, StudentKey obj) throws IOException {
        output.writeInt(obj.getGroupId());
        output.writeUTF(obj.getName());
    }

    @Override
    public void serialize(RandomAccessFile output, StudentKey obj) throws IOException {
        output.writeInt(obj.getGroupId());
        output.writeUTF(obj.getName());
    }

    @Override
    public StudentKey deserialize(DataInputStream input) throws IOException {
        int groupId = input.readInt();
        String name = input.readUTF();
        return new StudentKey(groupId, name);
    }

    @Override
    public StudentKey deserialize(RandomAccessFile input) throws IOException {
        int groupId = input.readInt();
        String name = input.readUTF();
        return new StudentKey(groupId, name);
    }
}
