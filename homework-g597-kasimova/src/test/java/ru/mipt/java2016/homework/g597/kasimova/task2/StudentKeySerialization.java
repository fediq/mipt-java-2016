package ru.mipt.java2016.homework.g597.kasimova.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Надежда on 03.11.2016.
 */
public class StudentKeySerialization implements MSerialization<StudentKey> {
    @Override
    public void serializeToStream(StudentKey value, DataOutput outStream) {
        try {
            outStream.writeInt(value.getGroupId());
            outStream.writeUTF(value.getName());
        } catch (IOException exp) {
            System.out.println(exp.getMessage());
        }
    }

    @Override
    public StudentKey deserializeFromStream(DataInput inStream) {
        try {
            return new StudentKey(inStream.readInt(), inStream.readUTF());
        } catch (IOException exp) {
            System.out.println(exp.getMessage());
        }
        return null;
    }
}