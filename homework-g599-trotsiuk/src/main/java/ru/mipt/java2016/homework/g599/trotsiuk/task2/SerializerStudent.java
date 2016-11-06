package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;



public class SerializerStudent implements Serializer<Student> {

    @Override
    public void serializeWrite(Student value, RandomAccessFile dbFile) throws IOException {
        dbFile.writeInt(value.getGroupId());
        dbFile.writeInt(value.getName().getBytes("UTF-8").length);
        dbFile.write(value.getName().getBytes("UTF-8"));
        dbFile.writeInt(value.getHometown().getBytes("UTF-8").length);
        dbFile.write(value.getHometown().getBytes("UTF-8"));
        dbFile.writeLong(value.getBirthDate().getTime());
        dbFile.writeBoolean(value.isHasDormitory());
        dbFile.writeDouble(value.getAverageScore());

    }

    @Override
    public Student deserializeRead(RandomAccessFile dbFile) throws IOException {
        int id = dbFile.readInt();
        int nameLength = dbFile.readInt();
        byte[] name = new byte[nameLength];
        dbFile.read(name, 0, nameLength);
        int hometownLength = dbFile.readInt();
        byte[] hometown = new byte[hometownLength];
        dbFile.read(hometown, 0, hometownLength);
        return new Student(id, new String(name, "UTF-8"), new String(hometown, "UTF-8"),
                new Date(dbFile.readLong()), dbFile.readBoolean(), dbFile.readDouble());
    }
}
