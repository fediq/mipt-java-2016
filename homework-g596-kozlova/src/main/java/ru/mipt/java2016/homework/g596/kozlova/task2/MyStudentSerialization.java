package ru.mipt.java2016.homework.g596.kozlova.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public class MyStudentSerialization implements MySerialization<Student> {

    @Override
    public Student read(DataInputStream readFromFile) throws IOException {
        int groupId = readFromFile.readInt();
        String name = readFromFile.readUTF();
        String homeTown = readFromFile.readUTF();
        Date birthDate = new Date(readFromFile.readLong());
        boolean hasDormitory = readFromFile.readBoolean();
        double averageScore = readFromFile.readDouble();
        return new Student(groupId, name, homeTown, birthDate, hasDormitory, averageScore);
    }

    @Override
    public void write(DataOutputStream writeToFile, Student student) throws IOException {
        writeToFile.writeInt(student.getGroupId());
        writeToFile.writeUTF(student.getName());
        writeToFile.writeUTF(student.getHometown());
        writeToFile.writeLong(student.getBirthDate().getTime());
        writeToFile.writeBoolean(student.isHasDormitory());
        writeToFile.writeDouble(student.getAverageScore());
    }
}