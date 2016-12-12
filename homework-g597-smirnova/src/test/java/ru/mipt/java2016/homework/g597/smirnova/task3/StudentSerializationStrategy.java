package ru.mipt.java2016.homework.g597.smirnova.task3;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Elena Smirnova on 21.11.2016.
 */
public class StudentSerializationStrategy implements SerializationStrategy<Student>{
    @Override
    public void writeToStream(DataOutput s, Student value) throws IOException {
        s.writeInt(value.getGroupId());
        s.writeUTF(value.getName());
        s.writeUTF(value.getHometown());
        s.writeLong(value.getBirthDate().getTime());
        s.writeBoolean(value.isHasDormitory());
        s.writeDouble(value.getAverageScore());
    }

    @Override
    public Student readFromStream(DataInput input) throws IOException {
        int groupID = input.readInt();
        String name = input.readUTF();
        String hometown = input.readUTF();
        Date birthDate = new Date(input.readLong());
        Boolean hasDormitory = input.readBoolean();
        Double averageScore = input.readDouble();
        return new Student(groupID, name, hometown, birthDate, hasDormitory, averageScore);
    }
}
