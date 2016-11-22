package ru.mipt.java2016.homework.g597.spirin.task3;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by whoami on 11/22/16.
 */
public class StudentKeySerializer implements SerializationStrategy<StudentKey> {

    private static class SingletonHolder {
        static final StudentKeySerializer HOLDER_INSTANCE = new StudentKeySerializer();
    }

    static StudentKeySerializer getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    private final IntegerSerializer integerSerializer = IntegerSerializer.getInstance();
    private final StringSerializer stringSerializer = StringSerializer.getInstance();

    @Override
    public StudentKey read(RandomAccessFile file) throws IOException {
        int groupID = integerSerializer.read(file);
        String name = stringSerializer.read(file);
        return new StudentKey(groupID, name);
    }

    @Override
    public void write(RandomAccessFile file, StudentKey object) throws IOException {
        integerSerializer.write(file, object.getGroupId());
        stringSerializer.write(file, object.getName());
    }
}
