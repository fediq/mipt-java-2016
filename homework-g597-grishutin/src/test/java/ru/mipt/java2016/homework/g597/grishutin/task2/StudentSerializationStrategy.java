package ru.mipt.java2016.homework.g597.grishutin.task2;


import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.IOException;
import java.io.RandomAccessFile;

/*
    int groupId,
    String name,
    String hometown,
    Date birthDate,
    boolean hasDormitory,
    double averageScore
 */
class StudentSerializationStrategy implements SerializationStrategy<Student> {
    private IntegerSerializationStrategy integerSerializationStrategy = IntegerSerializationStrategy.getInstance();
    private StringSerializationStrategy stringSerializationStrategy = StringSerializationStrategy.getInstance();
    private DateSerializationStrategy dateSerializationStrategy = DateSerializationStrategy.getInstance();
    private BooleanSerializationStrategy booleanSerializationStrategy = BooleanSerializationStrategy.getInstance();
    private DoubleSerializationStrategy doubleSerializationStrategy = DoubleSerializationStrategy.getInstance();

    @Override
    public void serialize(Student student, RandomAccessFile raf) throws IOException {
        integerSerializationStrategy.serialize(student.getGroupId(), raf);
        stringSerializationStrategy.serialize(student.getName(), raf);
        stringSerializationStrategy.serialize(student.getHometown(), raf);
        dateSerializationStrategy.serialize(student.getBirthDate(), raf);
        booleanSerializationStrategy.serialize(student.isHasDormitory(), raf);
        doubleSerializationStrategy.serialize(student.getAverageScore(), raf);
    }

    @Override
    public Student deserialize(RandomAccessFile raf) throws IOException {
        return new Student(integerSerializationStrategy.deserialize(raf),
                stringSerializationStrategy.deserialize(raf),
                stringSerializationStrategy.deserialize(raf),
                dateSerializationStrategy.deserialize(raf),
                booleanSerializationStrategy.deserialize(raf),
                doubleSerializationStrategy.deserialize(raf));
    }
}
