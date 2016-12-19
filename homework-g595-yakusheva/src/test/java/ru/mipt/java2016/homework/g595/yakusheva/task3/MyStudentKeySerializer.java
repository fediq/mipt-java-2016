package ru.mipt.java2016.homework.g595.yakusheva.task3;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Софья on 27.10.2016.
 */
public class MyStudentKeySerializer implements MySecondSerializerInterface<StudentKey> {
    @Override
    public void serializeToStream(DataOutputStream dataOutputStream, StudentKey o) throws IOException {
        dataOutputStream.writeInt(o.getGroupId());
        dataOutputStream.writeUTF(o.getName());
    }

    @Override
    public StudentKey deserializeFromStream(DataInputStream dataInputStream) throws IOException {
        return new StudentKey(dataInputStream.readInt(), dataInputStream.readUTF());
    }
}
