package ru.mipt.java2016.homework.g594.ishkhanyan.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;


public class MyStudentSerialization implements MySerialization<Student> {
    @Override
    public void writeToFile(Student object, DataOutputStream file) throws IOException {
        file.writeInt(object.getGroupId());
        file.writeUTF(object.getName());
        file.writeUTF(object.getHometown());
        file.writeLong(object.getBirthDate().getTime()); // date -> (long)milliseconds
        file.writeBoolean(object.isHasDormitory());
        file.writeDouble(object.getAverageScore());
    }

    @Override
    public Student readFromFile(DataInputStream file) throws IOException {
        Integer groupId = file.readInt();
        String name = file.readUTF();
        String homeTown = file.readUTF();
        Date date = new Date(file.readLong()); // (long)milliseconds -> date
        Boolean hasDormitory = file.readBoolean();
        Double averageScore = file.readDouble();
        return new Student(groupId, name, homeTown, date, hasDormitory, averageScore);
    }
}
