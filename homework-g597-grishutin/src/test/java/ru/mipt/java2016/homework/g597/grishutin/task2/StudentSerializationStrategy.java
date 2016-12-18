package ru.mipt.java2016.homework.g597.grishutin.task2;


import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/*
    int groupId,
    String name,
    String hometown,
    Date birthDate,
    boolean hasDormitory,
    double averageScore
 */
public class StudentSerializationStrategy implements SerializationStrategy<Student> {
    private final IntegerSerializer integerSerializer = IntegerSerializer.getInstance();
    private final StringSerializer stringSerializer = StringSerializer.getInstance();
    private final DateSerializer dateSerializer = DateSerializer.getInstance();
    private final BooleanSerializer booleanSerializer = BooleanSerializer.getInstance();
    private final DoubleSerializer doubleSerializer = DoubleSerializer.getInstance();

    @Override
    public void serialize(Student student, DataOutput raf) throws IOException {
        integerSerializer.serialize(student.getGroupId(), raf);
        stringSerializer.serialize(student.getName(), raf);
        stringSerializer.serialize(student.getHometown(), raf);
        dateSerializer.serialize(student.getBirthDate(), raf);
        booleanSerializer.serialize(student.isHasDormitory(), raf);
        doubleSerializer.serialize(student.getAverageScore(), raf);
    }

    @Override
    public Student deserialize(DataInput raf) throws IOException {
        return new Student(integerSerializer.deserialize(raf),
                stringSerializer.deserialize(raf),
                stringSerializer.deserialize(raf),
                dateSerializer.deserialize(raf),
                booleanSerializer.deserialize(raf),
                doubleSerializer.deserialize(raf));
    }

    @Override
    public Long bytesSize(Student value) {
        return integerSerializer.bytesSize(value.getGroupId()) +
                stringSerializer.bytesSize(value.getName()) +
                stringSerializer.bytesSize(value.getHometown()) +
                dateSerializer.bytesSize(value.getBirthDate()) +
                booleanSerializer.bytesSize(value.isHasDormitory()) +
                doubleSerializer.bytesSize(value.getAverageScore());
    }
}
