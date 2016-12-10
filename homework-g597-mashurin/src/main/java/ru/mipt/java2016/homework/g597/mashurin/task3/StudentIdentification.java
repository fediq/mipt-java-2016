package ru.mipt.java2016.homework.g597.mashurin.task3;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.Date;

public class StudentIdentification implements Identification<Student> {

    public static StudentIdentification get() {
        return new StudentIdentification();
    }

    @Override
    public void write(RandomAccessFile output, Student object) throws IOException {
        IntegerIdentification.get().write(output, object.getGroupId());
        StringIdentification.get().write(output, object.getName());
        StringIdentification.get().write(output, object.getHometown());
        DateIdentification.get().write(output, object.getBirthDate());
        BooleanIdentification.get().write(output, object.isHasDormitory());
        DoubleIdentification.get().write(output, object.getAverageScore());
    }

    @Override
    public Student read(RandomAccessFile input) throws IOException {
        int group = IntegerIdentification.get().read(input);
        String name = StringIdentification.get().read(input);
        String hometown = StringIdentification.get().read(input);
        Date birthDate = DateIdentification.get().read(input);
        boolean hasDormitory = BooleanIdentification.get().read(input);
        double averageScore = DoubleIdentification.get().read(input);
        return new Student(group, name, hometown, birthDate, hasDormitory, averageScore);
    }
}
