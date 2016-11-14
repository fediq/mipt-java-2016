package ru.mipt.java2016.homework.g594.rubanenko.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by king on 31.10.16.
 */

/* ! Special class for serialization of "MyStudentKey" */
public class MyStudentKeySerializer implements MySerializer<StudentKey> {
    /* ! Write method */
    @Override
    public void serializeToStream(DataOutputStream output, StudentKey value) throws IOException {
        output.writeInt(value.getGroupId());
        output.writeUTF(value.getName());
    }

    /* ! Read method */
    @Override
    public StudentKey deserializeFromStream(DataInputStream input) throws IOException {
        return new StudentKey(input.readInt(), input.readUTF());
    }
}
