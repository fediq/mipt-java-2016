package ru.mipt.java2016.homework.g597.mashurin.task3;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.RandomAccessFile;
import java.io.IOException;

public class StudentKeyIdentification implements Identification<StudentKey> {

    public static StudentKeyIdentification get() {
        return new StudentKeyIdentification();
    }

    @Override
    public void write(RandomAccessFile output, StudentKey object) throws IOException {
        IntegerIdentification.get().write(output, object.getGroupId());
        StringIdentification.get().write(output, object.getName());
    }

    @Override
    public StudentKey read(RandomAccessFile input) throws IOException {
        int group = IntegerIdentification.get().read(input);
        String name = StringIdentification.get().read(input);
        return new StudentKey(group, name);
    }
}
