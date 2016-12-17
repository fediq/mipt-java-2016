package ru.mipt.java2016.homework.g597.nasretdinov.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by isk on 31.10.16.
 */
public class StudentSerializer implements SerializerInterface<Student> {
    @Override
    public void write(DataOutputStream stream, Student studentData) throws IOException {
        stream.writeInt(studentData.getGroupId());
        stream.writeUTF(studentData.getName());
        stream.writeUTF(studentData.getHometown());
        stream.writeLong((studentData.getBirthDate()).getTime());
        stream.writeBoolean(studentData.isHasDormitory());
        stream.writeDouble(studentData.getAverageScore());
    }

    @Override
    public Student read(DataInputStream stream) throws IOException {
        return new Student(stream.readInt(), stream.readUTF(),
                stream.readUTF(), new Date(stream.readLong()),
                stream.readBoolean(), stream.readDouble());
    }
}