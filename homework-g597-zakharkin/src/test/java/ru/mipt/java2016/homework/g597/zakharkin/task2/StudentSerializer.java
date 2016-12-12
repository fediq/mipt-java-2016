package ru.mipt.java2016.homework.g597.zakharkin.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Serialization strategy for Student type
 *
 * @autor Ilya Zakharkin
 * @since 31.10.16.
 */
public class StudentSerializer implements Serializer<Student> {
    private StudentSerializer() {
    }

    private static class InstanceHolder {
        public static final StudentSerializer INSTANCE = new StudentSerializer();
    }

    public static StudentSerializer getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public void write(DataOutput file, Student student) throws IOException {
        BooleanSerializer booleanSerializer = BooleanSerializer.getInstance();
        IntegerSerializer integerSerializer = IntegerSerializer.getInstance();
        DateSerializer dateSerializer = DateSerializer.getInstance();
        StringSerializer stringSerializer = StringSerializer.getInstance();
        DoubleSerializer doubleSerializer = DoubleSerializer.getInstance();

        integerSerializer.write(file, student.getGroupId());
        stringSerializer.write(file, student.getName());
        stringSerializer.write(file, student.getHometown());
        dateSerializer.write(file, student.getBirthDate());
        booleanSerializer.write(file, student.isHasDormitory());
        doubleSerializer.write(file, student.getAverageScore());
    }

    @Override
    public Student read(DataInput file) throws IOException {
        BooleanSerializer booleanSerializer = BooleanSerializer.getInstance();
        IntegerSerializer integerSerializer = IntegerSerializer.getInstance();
        DateSerializer dateSerializer = DateSerializer.getInstance();
        StringSerializer stringSerializer = StringSerializer.getInstance();
        DoubleSerializer doubleSerializer = DoubleSerializer.getInstance();

        Integer groupId = integerSerializer.read(file);
        String name = stringSerializer.read(file);
        String hometown = stringSerializer.read(file);
        Date birthDate = dateSerializer.read(file);
        Boolean hasDormitory = booleanSerializer.read(file);
        Double averageScore = doubleSerializer.read(file);

        return new Student(groupId, name, hometown, birthDate, hasDormitory, averageScore);
    }
}
