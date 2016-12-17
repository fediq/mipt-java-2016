package ru.mipt.java2016.homework.g596.kozlova.task3;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

public class MyStudentSerialization implements MySerialization<Student> {
    @Override
    public void write(Student obj, DataOutput output) throws IOException {
        output.writeInt(obj.getGroupId());
        output.writeUTF(obj.getName());
        output.writeUTF(obj.getHometown());
        output.writeLong(obj.getBirthDate().getTime());
        output.writeBoolean(obj.isHasDormitory());
        output.writeDouble(obj.getAverageScore());
    }

    @Override
    public Student read(DataInput input) throws IOException {
        int groupId = input.readInt();
        String name = input.readUTF();
        String hometown = input.readUTF();
        Date birthDate = new Date(input.readLong());
        boolean hasDormitory = input.readBoolean();
        double averageScore = input.readDouble();
        return new Student(groupId, name, hometown, birthDate, hasDormitory, averageScore);
    }
}