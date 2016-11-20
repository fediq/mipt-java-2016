package ru.mipt.java2016.homework.g597.smirnova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import ru.mipt.java2016.homework.tests.task2.Student;

/**
 * Created by Elena Smirnova on 31.10.2016.
 */
public class StudentSerializationStrategy implements SerializationStrategy<Student> {
    @Override
    public void writeToStream(DataOutputStream s, Student value) throws IOException {
        s.writeInt(value.getGroupId());
        s.writeUTF(value.getName());
        s.writeUTF(value.getHometown());
        s.writeLong(value.getBirthDate().getTime());
        s.writeBoolean(value.isHasDormitory());
        s.writeDouble(value.getAverageScore());
    }

    @Override
    public Student readFromStream(DataInputStream input) throws IOException {
        int groupID = input.readInt();
        String name = input.readUTF();
        String hometown = input.readUTF();
        Date birthDate = new Date(input.readLong());
        Boolean hasDormitory = input.readBoolean();
        Double averageScore = input.readDouble();
        return new Student(groupID, name, hometown, birthDate, hasDormitory, averageScore);
    }
}
