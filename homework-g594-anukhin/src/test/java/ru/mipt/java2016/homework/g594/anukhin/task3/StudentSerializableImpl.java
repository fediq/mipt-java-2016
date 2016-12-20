package ru.mipt.java2016.homework.g594.anukhin.task3;

import ru.mipt.java2016.homework.g594.anukhin.task3.Serializable;
import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Created by clumpytuna on 29.10.16.
 */
public class StudentSerializableImpl implements Serializable<Student> {

    @Override
    public void serialize(DataOutputStream output, Student obj) throws IOException {
        output.writeInt(obj.getGroupId());
        output.writeUTF(obj.getName());
        output.writeUTF(obj.getHometown());
        output.writeLong(obj.getBirthDate().getTime());
        output.writeBoolean(obj.isHasDormitory());
        output.writeDouble(obj.getAverageScore());
    }

    @Override
    public void serialize(RandomAccessFile output, Student obj) throws IOException {
        output.writeInt(obj.getGroupId());
        output.writeUTF(obj.getName());
        output.writeUTF(obj.getHometown());
        output.writeLong(obj.getBirthDate().getTime());
        output.writeBoolean(obj.isHasDormitory());
        output.writeDouble(obj.getAverageScore());
    }

    @Override
    public Student deserialize(DataInputStream input) throws IOException {
        int groupId = input.readInt();
        String name = input.readUTF();
        String homeTown = input.readUTF();
        Date birthDate = new Date(input.readLong());
        boolean dormitory = input.readBoolean();
        double averageScore = input.readDouble();
        return new Student(groupId, name, homeTown, birthDate, dormitory, averageScore);
    }

    @Override
    public Student deserialize(RandomAccessFile input) throws IOException {
        int groupId = input.readInt();
        String name = input.readUTF();
        String homeTown = input.readUTF();
        Date birthDate = new Date(input.readLong());
        boolean dormitory = input.readBoolean();
        double averageScore = input.readDouble();
        return new Student(groupId, name, homeTown, birthDate, dormitory, averageScore);
    }
}
