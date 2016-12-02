package ru.mipt.java2016.homework.g594.rubanenko.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by king on 31.10.16.
 */

/* ! Special class for serialization of "MyStudent" */
public class MyStudentSerializer implements MySerializer<Student> {
    /* ! Write method */
    @Override
    public void serializeToStream(DataOutputStream output, Student value) throws IOException {
        output.writeInt(value.getGroupId());
        output.writeUTF(value.getName());
        output.writeUTF(value.getHometown());
        output.writeLong(value.getBirthDate().getTime());
        output.writeBoolean(value.isHasDormitory());
        output.writeDouble(value.getAverageScore());
    }

    /* ! Read method */
    @Override
    public Student deserializeFromStream(DataInputStream input) throws IOException {
        return new Student(input.readInt(), input.readUTF(), input.readUTF(),
                new Date(input.readLong()), input.readBoolean(), input.readDouble());
    }
}
