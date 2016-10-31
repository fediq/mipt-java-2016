package ru.mipt.java2016.homework.g594.vishnyakova.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Nina on 27.10.16.
 */
public class StudentKeySerializationStrategy extends SerializationStrategy<StudentKey> {

    @Override
    public StudentKey read(DataInputStream rd) throws IOException {
        Integer grId = rd.readInt();
        String stName = rd.readUTF();
        return new StudentKey(grId, stName);
    }

    @Override
    public void write(DataOutputStream wr, StudentKey obj) throws IOException {
        wr.writeInt(obj.getGroupId());
        wr.writeUTF(obj.getName());
    }
}