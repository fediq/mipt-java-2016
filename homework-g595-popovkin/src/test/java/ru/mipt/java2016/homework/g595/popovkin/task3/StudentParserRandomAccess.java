package ru.mipt.java2016.homework.g595.popovkin.task3;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Created by Howl on 17.11.2016.
 */
public class StudentParserRandomAccess implements ParserInterface<Student> {

    @Override
    public void serialize(Student arg, RandomAccessFile out) throws IOException {
        out.writeInt(arg.getGroupId());
        out.writeUTF(arg.getName());
        out.writeUTF(arg.getHometown());
        Date birthDate = arg.getBirthDate();
        out.writeLong(birthDate.getTime());
        out.writeBoolean(arg.isHasDormitory());
        out.writeDouble(arg.getAverageScore());
    }

    @Override
    public Student deserialize(RandomAccessFile in) throws IOException {
        return new Student(in.readInt(), in.readUTF(), in.readUTF(),
                new Date(in.readLong()), in.readBoolean(), in.readDouble());
    }
}