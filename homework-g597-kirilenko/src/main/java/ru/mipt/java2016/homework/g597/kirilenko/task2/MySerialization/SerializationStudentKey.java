package ru.mipt.java2016.homework.g597.kirilenko.task2.MySerialization;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Natak on 29.10.2016.
 */
public class SerializationStudentKey implements MySerialization<StudentKey> {
    private SerializationInteger serialize_int = SerializationInteger.getSerialization();
    private SerializationString serialize_str = SerializationString.getSerialization();
    static SerializationStudentKey serialize = new SerializationStudentKey();
    private SerializationStudentKey() { };
    public static SerializationStudentKey getSerialization() {
        return serialize;
    }

    @Override
    public void write(RandomAccessFile file, StudentKey value) throws IOException {
        serialize_int.write(file, value.getGroupId());
        serialize_str.write(file, value.getName());
    }

    @Override
    public StudentKey read(RandomAccessFile file) throws IOException {
        Integer group = serialize_int.read(file);
        String name = serialize_str.read(file);
        StudentKey value = new StudentKey(group, name);
        return value;
    }
}
