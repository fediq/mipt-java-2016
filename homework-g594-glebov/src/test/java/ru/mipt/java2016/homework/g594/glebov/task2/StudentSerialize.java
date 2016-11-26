package ru.mipt.java2016.homework.g594.glebov.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by daniil on 16.11.16.
 */

public class StudentSerialize implements MySerializer<Student> {
    @Override
    public void streamSerialize(Student object, DataOutputStream output) throws IOException {
        output.writeInt(object.getGroupId());
        output.writeUTF(object.getName());
        output.writeUTF(object.getHometown());
        output.writeLong(object.getBirthDate().getTime());
        output.writeBoolean(object.isHasDormitory());
        output.writeDouble(object.getAverageScore());
    }

    @Override
    public Student streamDeserialize(DataInputStream input) throws IOException {
        Integer groupId = input.readInt();
        String name = input.readUTF();
        String hometown = input.readUTF();
        Long ldate = input.readLong();
        Date date = new Date(ldate);
        Boolean hasDormitory = input.readBoolean();
        Double averageScore = input.readDouble();
        return new Student(groupId, name, hometown, date, hasDormitory, averageScore);
    }
}
