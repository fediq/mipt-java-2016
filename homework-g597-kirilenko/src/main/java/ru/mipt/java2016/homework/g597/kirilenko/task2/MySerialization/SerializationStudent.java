package ru.mipt.java2016.homework.g597.kirilenko.task2.MySerialization;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Created by Natak on 29.10.2016.
 */
public class SerializationStudent implements MySerialization<Student> {
    private SerializationInteger serialize_int = SerializationInteger.getSerialization();
    //private SerializationString serialize_str = SerializationString.getSerialization();
    private SerializationString serialize_str = SerializationString.getSerialization();
    private SerializationDate serialize_date = SerializationDate.getSerialization();
    private SerializationBoolean serialize_bool = SerializationBoolean.getSerialization();
    private SerializationDouble serialize_double = SerializationDouble.getSerialization();
    static SerializationStudent serialize = new SerializationStudent();
    private SerializationStudent() { };
    public static SerializationStudent getSerialization() {
        return serialize;
    }

    @Override
    public void write(RandomAccessFile file, Student value) throws IOException {
        serialize_int.write(file, value.getGroupId());
        serialize_str.write(file, value.getName());
        serialize_str.write(file, value.getHometown());
        serialize_date.write(file, value.getBirthDate());
        serialize_bool.write(file, value.isHasDormitory());
        serialize_double.write(file, value.getAverageScore());
    }

    @Override
    public Student read(RandomAccessFile file) throws IOException {
        Integer group = serialize_int.read(file);
        String name = serialize_str.read(file);
        String home = serialize_str.read(file);
        Date birth = serialize_date.read(file);
        Boolean dormitory = serialize_bool.read(file);
        Double score = serialize_double.read(file);
        Student value = new Student(group, name, home, birth, dormitory, score);
        return value;
    }
}
