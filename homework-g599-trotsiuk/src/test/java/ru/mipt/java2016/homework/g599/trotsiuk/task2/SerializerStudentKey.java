package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SerializerStudentKey implements Serializer<StudentKey> {


    @Override
    public void serializeWrite(StudentKey value, DataOutputStream stream) throws IOException {
        stream.writeInt(value.getGroupId());
        stream.writeInt(value.getName().length());
        stream.write(value.getName().getBytes("UTF-8"));

    }

    @Override
    public StudentKey deserializeRead(DataInputStream stream) throws IOException {
        int id = stream.readInt();
        int wordLength = stream.readInt();
        byte[] word = new byte[wordLength];
        stream.read(word);
        return new StudentKey(id, new String(word));
    }
}
