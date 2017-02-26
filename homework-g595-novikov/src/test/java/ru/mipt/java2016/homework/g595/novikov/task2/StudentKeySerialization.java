package ru.mipt.java2016.homework.g595.novikov.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by igor on 10/25/16.
 */
public class StudentKeySerialization extends MySerialization<StudentKey> {
    @Override
    public void serialize(DataOutput file, StudentKey object) throws IOException {
        serializeInteger(file, object.getGroupId());
        serializeString(file, object.getName());
    }

    @Override
    public StudentKey deserialize(DataInput file) throws IOException {
        return new StudentKey(deserializeInteger(file),
                deserializeString(file));
    }

    @Override
    public long getSizeSerialized(StudentKey object) {
        return getSizeSerializedInteger(object.getGroupId()) +
                getSizeSerializedString(object.getName());
    }
}
