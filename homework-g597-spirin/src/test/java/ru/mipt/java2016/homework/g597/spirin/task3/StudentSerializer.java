package ru.mipt.java2016.homework.g597.spirin.task3;

import ru.mipt.java2016.homework.tests.task2.Student;

import javax.xml.crypto.Data;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Created by whoami on 11/22/16.
 */
public class StudentSerializer implements SerializationStrategy<Student> {

    private static class SingletonHolder {
        static final StudentSerializer HOLDER_INSTANCE = new StudentSerializer();
    }

    static StudentSerializer getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    private final IntegerSerializer integerSerializer = IntegerSerializer.getInstance();
    private final StringSerializer stringSerializer = StringSerializer.getInstance();
    private final DateSerializer dateSerializer = DateSerializer.getInstance();
    private final BooleanSerializer booleanSerializer = BooleanSerializer.getInstance();
    private final DoubleSerializer doubleSerializer = DoubleSerializer.getInstance();

    @Override
    public Student read(DataInput file) throws IOException {
        int groupID = integerSerializer.read(file);
        String name = stringSerializer.read(file);
        String hometown = stringSerializer.read(file);
        Date birthDate = dateSerializer.read(file);
        boolean hasDormitory = booleanSerializer.read(file);
        double averageScore = doubleSerializer.read(file);
        return new Student(groupID, name, hometown, birthDate, hasDormitory, averageScore);
    }

    @Override
    public void write(DataOutput file, Student object) throws IOException {
        integerSerializer.write(file, object.getGroupId());
        stringSerializer.write(file, object.getName());
        stringSerializer.write(file, object.getHometown());
        dateSerializer.write(file, object.getBirthDate());
        booleanSerializer.write(file, object.isHasDormitory());
        doubleSerializer.write(file, object.getAverageScore());
    }
}
