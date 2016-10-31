package ru.mipt.java2016.homework.g596.grebenshchikova.task2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import ru.mipt.java2016.homework.tests.task2.Student;

/**
 * Created by liza on 31.10.16.
 */
public class StudentSerializer implements MySerializerInterface<Student> {
    private final StringSerializer stringSerializer = StringSerializer.getExample();
    private final DateSerializer dateSerializer = DateSerializer.getExample();
    private final BooleanSerializer booleanSerializer = BooleanSerializer.getExample();
    private final DoubleSerializer doubleSerializer = DoubleSerializer.getExample();
    private final IntegerSerializer integerSerializer = IntegerSerializer.getExample();

    @Override
    public void write(RandomAccessFile file, Student object) throws IOException {
        integerSerializer.write(file, object.getGroupId());
        stringSerializer.write(file, object.getName());
        stringSerializer.write(file, object.getHometown());
        dateSerializer.write(file, object.getBirthDate());
        booleanSerializer.write(file, object.isHasDormitory());
        doubleSerializer.write(file, object.getAverageScore());
    }

    @Override
    public Student read(RandomAccessFile file) throws IOException {
        int groupId = integerSerializer.read(file);
        String name = stringSerializer.read(file);
        String hometown = stringSerializer.read(file);
        Date birthDate = dateSerializer.read(file);
        boolean hasDormitory = booleanSerializer.read(file);
        double averageScore = doubleSerializer.read(file);
        return new Student(groupId, name, hometown, birthDate, hasDormitory, averageScore);
    }

    private static final StudentSerializer Example = new StudentSerializer();

    public static StudentSerializer getExample() {
        return Example;
    }

    private StudentSerializer() {
    }
}


