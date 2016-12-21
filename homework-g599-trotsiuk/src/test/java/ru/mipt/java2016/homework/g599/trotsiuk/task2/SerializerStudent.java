package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.*;
import java.util.Date;



public class SerializerStudent implements Serializer<Student> {

    @Override
    public void serializeWrite(Student value, DataOutputStream stream) throws IOException {
        stream.writeInt(value.getGroupId());
        stream.writeUTF(value.getName());
        stream.writeUTF(value.getHometown());
        stream.writeLong(value.getBirthDate().getTime());
        stream.writeBoolean(value.isHasDormitory());
        stream.writeDouble(value.getAverageScore());

    }

    @Override
    public Student deserializeRead(DataInputStream stream) throws IOException {
        Student responceStudent = new Student(stream.readInt(), stream.readUTF(), stream.readUTF(),
                new Date(stream.readLong()), stream.readBoolean(),
                stream.readDouble());
        return responceStudent;
    }
}
