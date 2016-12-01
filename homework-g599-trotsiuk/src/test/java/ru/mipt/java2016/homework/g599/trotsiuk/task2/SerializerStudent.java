package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;



public class SerializerStudent implements Serializer<Student> {

    @Override
    public void serializeWrite(Student value, DataOutputStream stream) throws IOException {
        stream.writeInt(value.getGroupId());
        stream.writeInt(value.getName().length());
        stream.write(value.getName().getBytes("UTF-8"));
        stream.writeInt(value.getHometown().length());
        stream.write(value.getHometown().getBytes("UTF-8"));
        stream.writeLong(value.getBirthDate().getTime());
        stream.writeBoolean(value.isHasDormitory());
        stream.writeDouble(value.getAverageScore());

    }

    @Override
    public Student deserializeRead(DataInputStream stream) throws IOException {
        int id = stream.readInt();
        int nameLength = stream.readInt();
        byte[] name = new byte[nameLength];
        stream.read(name);
        int hometownLength = stream.readInt();
        byte[] hometown = new byte[hometownLength];
        stream.read(hometown);
        return new Student(id, new String(name), new String(hometown),
                new Date(stream.readLong()), stream.readBoolean(), stream.readDouble());
    }
}
