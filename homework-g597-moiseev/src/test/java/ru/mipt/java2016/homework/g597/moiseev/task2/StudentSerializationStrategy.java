package ru.mipt.java2016.homework.g597.moiseev.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Стратегия сериализации для Student
 *
 * @author Fedor Moiseev
 * @since 26.10.2016
 */
public class StudentSerializationStrategy implements SerializationStrategy<Student> {
    private static final StudentSerializationStrategy INSTANCE = new StudentSerializationStrategy();

    private final IntegerSerializationStrategy integerSerializationStrategy =
            IntegerSerializationStrategy.getInstance();
    private final StringSerializationStrategy stringSerializationStrategy =
            StringSerializationStrategy.getInstance();
    private final DateSerializationStrategy dateSerializationStrategy =
            DateSerializationStrategy.getInstance();
    private final DoubleSerializationStrategy doubleSerializationStrategy =
            DoubleSerializationStrategy.getInstance();
    private final BooleanSerializationStrategy booleanSerializationStrategy =
            BooleanSerializationStrategy.getInstance();

    public static StudentSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private StudentSerializationStrategy() {
    }

    @Override
    public void write(RandomAccessFile file, Student object) throws IOException {
        integerSerializationStrategy.write(file, object.getGroupId());
        stringSerializationStrategy.write(file, object.getName());
        stringSerializationStrategy.write(file, object.getHometown());
        dateSerializationStrategy.write(file, object.getBirthDate());
        booleanSerializationStrategy.write(file, object.isHasDormitory());
        doubleSerializationStrategy.write(file, object.getAverageScore());
    }

    @Override
    public Student read(RandomAccessFile file) throws IOException {
        int groupId = integerSerializationStrategy.read(file);
        String name = stringSerializationStrategy.read(file);
        String hometown = stringSerializationStrategy.read(file);
        Date birthDate = dateSerializationStrategy.read(file);
        boolean hasDormitory = booleanSerializationStrategy.read(file);
        double averageScore = doubleSerializationStrategy.read(file);
        return new Student(groupId, name, hometown, birthDate, hasDormitory, averageScore);
    }
}
