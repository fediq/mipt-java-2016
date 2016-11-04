package ru.mipt.java2016.homework.g596.grebenshchikova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
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
    public void write(DataOutput output, Student object) throws IOException {
        integerSerializer.write(output, object.getGroupId());
        stringSerializer.write(output, object.getName());
        stringSerializer.write(output, object.getHometown());
        dateSerializer.write(output, object.getBirthDate());
        booleanSerializer.write(output, object.isHasDormitory());
        doubleSerializer.write(output, object.getAverageScore());
    }

    @Override
    public Student read(DataInput input) throws IOException {
        int groupId = integerSerializer.read(input);
        String name = stringSerializer.read(input);
        String hometown = stringSerializer.read(input);
        Date birthDate = dateSerializer.read(input);
        boolean hasDormitory = booleanSerializer.read(input);
        double averageScore = doubleSerializer.read(input);
        return new Student(groupId, name, hometown, birthDate, hasDormitory, averageScore);
    }

    private static final StudentSerializer EXAMPLE = new StudentSerializer();

    public static StudentSerializer getExample() {
        return EXAMPLE;
    }

    private StudentSerializer() {
    }
}


