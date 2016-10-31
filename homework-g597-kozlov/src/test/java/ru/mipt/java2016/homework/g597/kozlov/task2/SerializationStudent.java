package ru.mipt.java2016.homework.g597.kozlov.task2;

/**
 * Created by Alexander on 31.10.2016.
 */

import ru.mipt.java2016.homework.tests.task2.Student;

import java.util.Date;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SerializationStudent implements Serialization<Student> {
    private final SerializationInteger sampleInteger = new SerializationInteger();
    private final SerializationString sampleString = new SerializationString();
    private final SerializationDate sampleDate = new SerializationDate();
    private final SerializationBoolean sampleBoolean = new SerializationBoolean();
    private final SerializationDouble sampleDouble = new SerializationDouble();

    @Override
    public Student read(RandomAccessFile file) throws IOException {
        int groupId = sampleInteger.read(file);
        String name = sampleString.read(file);
        String hometown = sampleString.read(file);
        Date birthDate = sampleDate.read(file);
        boolean hasDormitory = sampleBoolean.read(file);
        double averageScore = sampleDouble.read(file);
        return new Student(groupId, name, hometown, birthDate, hasDormitory, averageScore);
    }

    @Override
    public void write(RandomAccessFile file, Student object) throws IOException {
        sampleInteger.write(file, object.getGroupId());
        sampleString.write(file, object.getName());
        sampleString.write(file, object.getHometown());
        sampleDate.write(file, object.getBirthDate());
        sampleBoolean.write(file, object.isHasDormitory());
        sampleDouble.write(file, object.getAverageScore());
    }
}