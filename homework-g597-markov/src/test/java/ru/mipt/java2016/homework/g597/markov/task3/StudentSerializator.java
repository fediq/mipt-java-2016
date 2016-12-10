package ru.mipt.java2016.homework.g597.markov.task3;

/**
 * Created by Alexander on 25.11.2016.
 */


import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

public class StudentSerializator implements SerializationStrategy<Student> {

    private IntegerSerializator integerSerializator = new IntegerSerializator();
    private StringSerializator stringSerializator = new StringSerializator();
    private DateSerializator dateSerializator = new DateSerializator();
    private BooleanSerializator booleanSerializator = new BooleanSerializator();
    private DoubleSerializator doubleSerializator = new DoubleSerializator();

    @Override
    public Student read(RandomAccessFile fileName) throws IOException {
        int groupId = integerSerializator.read(fileName);
        String name = stringSerializator.read(fileName);
        String hometown = stringSerializator.read(fileName);
        Date birthday = dateSerializator.read(fileName);
        boolean hasDormitory = booleanSerializator.read(fileName);
        Double average = doubleSerializator.read(fileName);
        return new Student(groupId, name, hometown, birthday, hasDormitory, average);
    }

    @Override
    public void write(RandomAccessFile fileName, Student student) throws IOException {
        integerSerializator.write(fileName, student.getGroupId());
        stringSerializator.write(fileName, student.getName());
        stringSerializator.write(fileName, student.getHometown());
        dateSerializator.write(fileName, student.getBirthDate());
        booleanSerializator.write(fileName, student.isHasDormitory());
        doubleSerializator.write(fileName, student.getAverageScore());
    }
}
