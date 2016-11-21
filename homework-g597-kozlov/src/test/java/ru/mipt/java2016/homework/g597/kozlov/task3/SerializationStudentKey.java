package ru.mipt.java2016.homework.g597.kozlov.task3;

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
    public StudentKey read(RandomAccessFile file, long shift) throws IOException {
        file.seek(shift);
        int groupId = sampleInteger.read(file, file.getFilePointer());
        String name = sampleString.read(file, file.getFilePointer());
        return new StudentKey(groupId, name);
    }

    @Override
    public void write(RandomAccessFile file, StudentKey object, long shift) throws IOException {
        file.seek(shift);
        sampleInteger.write(file, object.getGroupId(), file.getFilePointer());
        sampleString.write(file, object.getName(), file.getFilePointer());
    }
}