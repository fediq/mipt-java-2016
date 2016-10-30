package ru.mipt.java2016.homework.g595.yakusheva.task2;

import java.io.*;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by Софья on 27.10.2016.
 */
public class MyStudentKeySerializer implements MyFirstSerializerInterface<StudentKey> {
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
