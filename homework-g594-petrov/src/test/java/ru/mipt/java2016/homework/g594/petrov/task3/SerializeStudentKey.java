package ru.mipt.java2016.homework.g594.petrov.task3;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInput;
import java.io.DataOutput;

/**
 * Created by philipp on 14.11.16.
 */
public class SerializeStudentKey implements InterfaceSerialization<StudentKey> {
    @Override
    public StudentKey readValue(DataInput inputStream) throws IllegalStateException {
        try {
            return new StudentKey(inputStream.readInt(), inputStream.readUTF());
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void writeValue(StudentKey obj, DataOutput outputStream) throws IllegalStateException {
        try {
            outputStream.writeInt(obj.getGroupId());
            outputStream.writeUTF(obj.getName());
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }
}
