package ru.mipt.java2016.homework.g597.kasimova.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Надежда on 03.11.2016.
 */
public class StudentSerialization implements MSerialization<Student> {
    @Override
    public void serializeToStream(Student value, DataOutput outStream) {
        try {
            outStream.writeInt(value.getGroupId());
            outStream.writeUTF(value.getName());
            outStream.writeUTF(value.getHometown());
            outStream.writeLong(value.getBirthDate().getTime());
            outStream.writeBoolean(value.isHasDormitory());
            outStream.writeDouble(value.getAverageScore());
        } catch (IOException exp) {
            System.out.println(exp.getMessage());
        }
    }

    @Override
    public Student deserializeFromStream(DataInput inStream) {
        try {
            return new Student(
                    inStream.readInt(),
                    inStream.readUTF(),
                    inStream.readUTF(),
                    new Date(inStream.readLong()),
                    inStream.readBoolean(),
                    inStream.readDouble()
            );
        } catch (IOException exp) {
            System.out.println(exp.getMessage());
        }
        return null;
    }
}
