package ru.mipt.java2016.homework.g597.kozlov.task3;

/**
 * Created by Alexander on 21.11.2016.
 */

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;

public class SerializationStudentKey implements Serialization<StudentKey> {
    private static final SerializationInteger SAMPLE_INTEGER = new SerializationInteger();
    private static final SerializationString SAMPLE_STRING = new SerializationString();

    @Override
    public StudentKey read(RandomAccessFile file, long shift) throws IOException {
        file.seek(shift);
        int groupId = SAMPLE_INTEGER.read(file, file.getFilePointer());
        String name = SAMPLE_STRING.read(file, file.getFilePointer());
        return new StudentKey(groupId, name);
    }

    @Override
    public void write(RandomAccessFile file, StudentKey object, long shift) throws IOException {
        file.seek(shift);
        SAMPLE_INTEGER.write(file, object.getGroupId(), file.getFilePointer());
        SAMPLE_STRING.write(file, object.getName(), file.getFilePointer());
    }
}