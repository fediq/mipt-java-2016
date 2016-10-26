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
public class StudentSerialization implements SerializationStrategy<Student> {
    private static StudentSerialization instance = new StudentSerialization();

    private IntegerSerialization integerSerialization = IntegerSerialization.getInstance();
    private StringSerialization stringSerialization = StringSerialization.getInstance();
    private DateSerialization dateSerialization = DateSerialization.getInstance();
    private DoubleSerialization doubleSerialization = DoubleSerialization.getInstance();
    private BooleanSerialization booleanSerialization = BooleanSerialization.getInstance();

    public static StudentSerialization getInstance() {
        return instance;
    }

    private StudentSerialization() {
    }

    @Override
    public void write(RandomAccessFile file, Student object) throws IOException {
        integerSerialization.write(file, object.getGroupId());
        stringSerialization.write(file, object.getName());
        stringSerialization.write(file, object.getHometown());
        dateSerialization.write(file, object.getBirthDate());
        booleanSerialization.write(file, object.isHasDormitory());
        doubleSerialization.write(file, object.getAverageScore());
    }

    @Override
    public Student read(RandomAccessFile file) throws IOException {
        int groupId = integerSerialization.read(file);
        String name = stringSerialization.read(file);
        String hometown = stringSerialization.read(file);
        Date birthDate = dateSerialization.read(file);
        boolean hasDormitory = booleanSerialization.read(file);
        double averageScore = doubleSerialization.read(file);
        return new Student(groupId, name, hometown, birthDate, hasDormitory, averageScore);
    }
}
