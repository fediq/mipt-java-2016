package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.*;

public class SerializerStudentKey implements Serializer<StudentKey> {


    @Override
    public void serializeWrite(StudentKey value, DataOutputStream stream) throws IOException {
        stream.writeInt(value.getGroupId());
        stream.writeUTF(value.getName());

    }

    @Override
    public StudentKey deserializeRead(DataInputStream stream) throws IOException {
        int id = stream.readInt();
        return new StudentKey(id, new String(stream.readUTF()));
    }
}
