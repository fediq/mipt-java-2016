package ru.mipt.java2016.homework.g597.kirilenko.task3;

import ru.mipt.java2016.homework.g597.kirilenko.task3.MySerialization.MySerialization;
import ru.mipt.java2016.homework.g597.kirilenko.task3.MySerialization.SerializationType;
import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Created by Natak on 01.11.2016.
 */
public class SerializationStudent implements MySerialization<Student> {
    private SerializationType.SerializationInteger serializeInt =
            SerializationType.SerializationInteger.getSerialization();
    private SerializationType.SerializationString serializeStr =
            SerializationType.SerializationString.getSerialization();
    private SerializationType.SerializationDate serializeDate =
            SerializationType.SerializationDate.getSerialization();
    private SerializationType.SerializationBoolean serializeBool =
            SerializationType.SerializationBoolean.getSerialization();
    private SerializationType.SerializationDouble serializeDouble =
            SerializationType.SerializationDouble.getSerialization();
    private static SerializationStudent serialize = new SerializationStudent();

    private SerializationStudent() {
    }

    public static SerializationStudent getSerialization() {
        return serialize;
    }

    private boolean isCorrect(Student value) {
        Integer group = value.getGroupId();
        String name = value.getName();
        Date birth = value.getBirthDate();
        int year = birth.getYear();
        Double score = value.getAverageScore();
        if (group <= 0 || name == "" || name == " " || score < 0 || year < -1900 || year > 116) {
            return false;
        }
        return true;
    }

    @Override
    public void write(RandomAccessFile file, Student value) throws IOException {
        serializeInt.write(file, value.getGroupId());
        serializeStr.write(file, value.getName());
        serializeStr.write(file, value.getHometown());
        serializeDate.write(file, value.getBirthDate());
        serializeBool.write(file, value.isHasDormitory());
        serializeDouble.write(file, value.getAverageScore());
    }

    @Override
    public Student read(RandomAccessFile file) throws IOException {
        Integer group = serializeInt.read(file);
        String name = serializeStr.read(file);
        String home = serializeStr.read(file);
        Date birth = serializeDate.read(file);
        Boolean dormitory = serializeBool.read(file);
        Double score = serializeDouble.read(file);
        Student value = new Student(group, name, home, birth, dormitory, score);
        if (isCorrect(value)) {
            return value;
        } else {
            throw new IOException("Incorrect value");
        }
    }
}
