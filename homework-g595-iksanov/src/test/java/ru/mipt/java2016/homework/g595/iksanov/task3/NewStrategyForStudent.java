package ru.mipt.java2016.homework.g595.iksanov.task3;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Эмиль
 */
public class NewStrategyForStudent implements NewSerializationStrategy<Student> {
    private static final NewStrategyForStudent INSTANCE = new NewStrategyForStudent();

    public static NewStrategyForStudent getInstance() {
        return INSTANCE;
    }

    private NewStrategyForStudent() {}

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
