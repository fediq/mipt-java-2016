package ru.mipt.java2016.homework.g595.tkachenko.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Dmitry on 30/10/2016.
 */
public class StudentSerialization extends Serialization<Student> {
    @Override
    public Student read(DataInputStream input) throws IOException {
        return new Student(input.readInt(), readString(input), readString(input), new Date(input.readLong()),
                input.readBoolean(), input.readDouble());
    }

    @Override
    public void write(DataOutputStream output, Student x) throws IOException {
        output.writeInt(x.getGroupId());
        writeString(output, x.getName());
        writeString(output, x.getHometown());
        output.writeLong(x.getBirthDate().getTime());
        output.writeBoolean(x.isHasDormitory());
        output.writeDouble(x.getAverageScore());
    }
}
