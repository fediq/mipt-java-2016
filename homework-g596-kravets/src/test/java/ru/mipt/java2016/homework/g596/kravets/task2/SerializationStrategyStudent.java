package ru.mipt.java2016.homework.g596.kravets.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import ru.mipt.java2016.homework.tests.task2.Student;


public class SerializationStrategyStudent implements MySerialization<Student> {

    @Override
    public void write(DataOutputStream output, Student data) throws IOException {
        output.writeInt(data.getGroupId());
        output.writeUTF(data.getName());
        output.writeUTF(data.getHometown());
        output.writeLong(data.getBirthDate().getTime());
        output.writeBoolean(data.isHasDormitory());
        output.writeDouble(data.getAverageScore());
    }

    @Override
    public Student read(DataInputStream input) throws IOException {
        int studentGroupID = input.readInt();
        String studentName = input.readUTF();
        String studentHometown = input.readUTF();
        Date studentBirthday = new Date(input.readLong());
        return new Student(studentGroupID, studentName, studentHometown, studentBirthday,
                input.readBoolean(), input.readDouble());
    }
}
