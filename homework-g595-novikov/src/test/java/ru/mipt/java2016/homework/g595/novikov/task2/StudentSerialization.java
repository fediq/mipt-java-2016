package ru.mipt.java2016.homework.g595.novikov.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by igor on 10/25/16.
 */
public class StudentSerialization extends MySerialization<Student> {
    @Override
    public void serialize(DataOutput file, Student object) throws IOException {
        serializeInteger(file, object.getGroupId());
        serializeString(file, object.getName());
        serializeString(file, object.getHometown());
        serializeDate(file, object.getBirthDate());
        serializeBoolean(file, object.isHasDormitory());
        serializeDouble(file, object.getAverageScore());
    }

    @Override
    public Student deserialize(DataInput file) throws IOException {
        return new Student(deserializeInteger(file),
                deserializeString(file),
                deserializeString(file),
                deserializeDate(file),
                deserializeBoolean(file),
                deserializeDouble(file));
    }

    @Override
    public long getSizeSerialized(Student object) {
        return getSizeSerializedInteger(object.getGroupId())
                + getSizeSerializedString(object.getName())
                + getSizeSerializedString(object.getHometown())
                + getSizeSerializedDate(object.getBirthDate())
                + getSizeSerializedBoolean(object.isHasDormitory())
                + getSizeSerializedDouble(object.getAverageScore());
    }
}
