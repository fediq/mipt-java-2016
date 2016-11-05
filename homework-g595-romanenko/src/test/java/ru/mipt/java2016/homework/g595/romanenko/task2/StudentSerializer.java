package ru.mipt.java2016.homework.g595.romanenko.task2;

import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.*;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task2.serialization
 *
 * @author Ilya I. Romanenko
 * @since 26.10.16
 **/
public class StudentSerializer implements SerializationStrategy<Student> {

    private static final StudentSerializer STUDENT_SERIALIZER = new StudentSerializer();

    public static StudentSerializer getInstance() {
        return STUDENT_SERIALIZER;
    }

    @Override
    public void serializeToStream(Student student, OutputStream outputStream) throws IOException {
        StudentKey studentKey = new StudentKey(student.getGroupId(), student.getName());
        StudentKeySerializer.getInstance().serializeToStream(studentKey, outputStream);

        StringSerializer.getInstance().serializeToStream(student.getHometown(), outputStream);

        DateSerializer.getInstance().serializeToStream(student.getBirthDate(), outputStream);

        byte isHasDormitory = (student.isHasDormitory() ? (byte) 1 : 0);
        outputStream.write(isHasDormitory);

        DoubleSerializer.getInstance().serializeToStream(student.getAverageScore(), outputStream);
    }

    @Override
    public int getBytesSize(Student student) {
        return IntegerSerializer.getInstance().getBytesSize(student.getGroupId()) +
                StringSerializer.getInstance().getBytesSize(student.getName()) +
                StringSerializer.getInstance().getBytesSize(student.getHometown()) +
                DateSerializer.getInstance().getBytesSize(student.getBirthDate()) +
                1 + //one byte for boolean value
                DoubleSerializer.getInstance().getBytesSize(student.getAverageScore());
    }

    @Override
    public Student deserializeFromStream(InputStream inputStream) throws IOException {
        StudentKey studentKey = StudentKeySerializer.getInstance().deserializeFromStream(inputStream);

        String hometown = StringSerializer.getInstance().deserializeFromStream(inputStream);

        Date birthDate = DateSerializer.getInstance().deserializeFromStream(inputStream);

        boolean isHasDormitory = (inputStream.read() != 0);

        Double averageScore = DoubleSerializer.getInstance().deserializeFromStream(inputStream);

        return new Student(studentKey.getGroupId(), studentKey.getName(),
                hometown, birthDate, isHasDormitory, averageScore);
    }
}