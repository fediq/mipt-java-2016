package ru.mipt.java2016.homework.g597.zakharkin.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.*;

/**
 * Serialization strategy for StudentKey type
 *
 * @autor Ilya Zakharkin
 * @since 31.10.16.
 */
public class StudentKeySerializer implements Serializer<StudentKey> {
    private StudentKeySerializer() {
    }

    private static class InstanceHolder {
        public static final StudentKeySerializer INSTANCE = new StudentKeySerializer();
    }

    public static StudentKeySerializer getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public void write(DataOutput file, StudentKey student) throws IOException {
        IntegerSerializer integerSerializer = IntegerSerializer.getInstance();
        StringSerializer stringSerializer = StringSerializer.getInstance();
        integerSerializer.write(file, student.getGroupId());
        stringSerializer.write(file, student.getName());
    }

    @Override
    public StudentKey read(DataInput file) throws IOException {
        IntegerSerializer integerSerializer = IntegerSerializer.getInstance();
        StringSerializer stringSerializer = StringSerializer.getInstance();
        int groupId = integerSerializer.read(file);
        String studentName = stringSerializer.read(file);
        return new StudentKey(groupId, studentName);
    }
}
