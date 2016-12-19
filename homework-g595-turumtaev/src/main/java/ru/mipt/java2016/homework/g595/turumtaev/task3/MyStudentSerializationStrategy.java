package ru.mipt.java2016.homework.g595.turumtaev.task3;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.Date;

/**
 * Created by galim on 20.11.2016.
 */
public class MyStudentSerializationStrategy implements MySerializationStrategy<Student> {
    private static final MyStudentSerializationStrategy INSTANCE = new MyStudentSerializationStrategy();

    public static MyStudentSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private MyStudentSerializationStrategy() {
    }

    @Override
    public Long write(Student value, RandomAccessFile output) throws IOException {
        Long offset = output.getFilePointer();
        output.writeInt(value.getGroupId());
        output.writeUTF(value.getName());
        output.writeUTF(value.getHometown());
        output.writeLong(value.getBirthDate().getTime());
        output.writeBoolean(value.isHasDormitory());
        output.writeDouble(value.getAverageScore());
        return offset;
    }

    @Override
    public Student read(RandomAccessFile input) throws IOException {
        Integer groupId = input.readInt();
        String name = input.readUTF();
        String hometown = input.readUTF();
        Date birthDate =  new Date(input.readLong());
        Boolean isHasDormitory = input.readBoolean();
        Double averageScore = input.readDouble();

        return new Student(groupId, name, hometown, birthDate, isHasDormitory, averageScore);
    }
}

