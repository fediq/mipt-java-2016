package ru.mipt.java2016.homework.g597.kozlov.task3;

/**
 * Created by Alexander on 21.11.2016.
 */

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

public class SerializationStudent implements Serialization<Student> {
    private static final SerializationInteger SAMPLE_INTEGER = new SerializationInteger();
    private static final SerializationString SAMPLE_STRING = new SerializationString();
    private static final SerializationDate SAMPLE_DATA = new SerializationDate();
    private static final SerializationBoolean SAMPLE_BOOLEAN = new SerializationBoolean();
    private static final SerializationDouble SAMPLE_DOUBLE = new SerializationDouble();

    @Override
    public Student read(RandomAccessFile file, long shift) throws IOException {
        file.seek(shift);
        int groupId = SAMPLE_INTEGER.read(file, file.getFilePointer());
        String name = SAMPLE_STRING.read(file, file.getFilePointer());
        String hometown = SAMPLE_STRING.read(file, file.getFilePointer());
        Date birthDate = SAMPLE_DATA.read(file, file.getFilePointer());
        boolean hasDormitory = SAMPLE_BOOLEAN.read(file, file.getFilePointer());
        double averageScore = SAMPLE_DOUBLE.read(file, file.getFilePointer());
        return new Student(groupId, name, hometown, birthDate, hasDormitory, averageScore);
    }

    @Override
    public void write(RandomAccessFile file, Student object, long shift) throws IOException {
        file.seek(shift);
        SAMPLE_INTEGER.write(file, object.getGroupId(), file.getFilePointer());
        SAMPLE_STRING.write(file, object.getName(), file.getFilePointer());
        SAMPLE_STRING.write(file, object.getHometown(), file.getFilePointer());
        SAMPLE_DATA.write(file, object.getBirthDate(), file.getFilePointer());
        SAMPLE_BOOLEAN.write(file, object.isHasDormitory(), file.getFilePointer());
        SAMPLE_DOUBLE.write(file, object.getAverageScore(), file.getFilePointer());
    }
}