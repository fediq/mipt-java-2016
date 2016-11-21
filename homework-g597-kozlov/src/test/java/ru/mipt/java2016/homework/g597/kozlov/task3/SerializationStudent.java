package ru.mipt.java2016.homework.g597.kozlov.task3;

/**
 * Created by Alexander on 21.11.2016.
 */

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

public class SerializationStudent implements Serialization<Student> {
    private final SerializationInteger sampleInteger = new SerializationInteger();
    private final SerializationString sampleString = new SerializationString();
    private final SerializationDate sampleDate = new SerializationDate();
    private final SerializationBoolean sampleBoolean = new SerializationBoolean();
    private final SerializationDouble sampleDouble = new SerializationDouble();

    @Override
    public Student read(RandomAccessFile file, long shift) throws IOException {
        file.seek(shift);
        int groupId = sampleInteger.read(file, file.getFilePointer());
        String name = sampleString.read(file, file.getFilePointer());
        String hometown = sampleString.read(file, file.getFilePointer());
        Date birthDate = sampleDate.read(file, file.getFilePointer());
        boolean hasDormitory = sampleBoolean.read(file, file.getFilePointer());
        double averageScore = sampleDouble.read(file, file.getFilePointer());
        return new Student(groupId, name, hometown, birthDate, hasDormitory, averageScore);
    }

    @Override
    public void write(RandomAccessFile file, Student object, long shift) throws IOException {
        file.seek(shift);
        sampleInteger.write(file, object.getGroupId(), file.getFilePointer());
        sampleString.write(file, object.getName(), file.getFilePointer());
        sampleString.write(file, object.getHometown(), file.getFilePointer());
        sampleDate.write(file, object.getBirthDate(), file.getFilePointer());
        sampleBoolean.write(file, object.isHasDormitory(), file.getFilePointer());
        sampleDouble.write(file, object.getAverageScore(), file.getFilePointer());
    }
}