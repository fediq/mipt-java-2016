package ru.mipt.java2016.homework.g595.turumtaev.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by galim on 31.10.2016.
 */
public class MyStudentSerializationStrategy implements MySerializationStrategy<Student> {
    private static final MyStudentSerializationStrategy INSTANCE = new MyStudentSerializationStrategy();

    public static MyStudentSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private MyStudentSerializationStrategy() {
    }

    @Override
    public void write(Student value, DataOutputStream output) throws IOException {
        output.writeInt(value.getGroupId());
        output.writeUTF(value.getName());
        output.writeUTF(value.getHometown());
        output.writeLong(value.getBirthDate().getTime());
        output.writeBoolean(value.isHasDormitory());
        output.writeDouble(value.getAverageScore());
    }

    @Override
    public Student read(DataInputStream input) throws IOException {
        Integer groupId = input.readInt();
        String name = input.readUTF();
        String hometown = input.readUTF();
        Date birthDate =  new Date(input.readLong());
        Boolean isHasDormitory = input.readBoolean();
        Double averageScore = input.readDouble();

        return new Student(groupId, name, hometown, birthDate, isHasDormitory, averageScore);
    }
}
