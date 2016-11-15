package ru.mipt.java2016.homework.g597.kozlov.task2;

/**
 * Created by Alexander on 31.10.2016.
 */

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;

public class SerializationStudentKey implements Serialization<StudentKey> {
    private final SerializationInteger sampleInteger = new SerializationInteger();
    private final SerializationString sampleString = new SerializationString();

    @Override
    public StudentKey read(RandomAccessFile file) throws IOException {
        int groupId = sampleInteger.read(file);
        String name = sampleString.read(file);
        return new StudentKey(groupId, name);
    }

    @Override
    public void write(RandomAccessFile file, StudentKey object) throws IOException {
        sampleInteger.write(file, object.getGroupId());
        sampleString.write(file, object.getName());
    }
}