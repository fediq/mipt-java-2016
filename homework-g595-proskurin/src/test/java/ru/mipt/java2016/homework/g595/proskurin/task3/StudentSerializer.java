package ru.mipt.java2016.homework.g595.proskurin.task3;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Date;

public class StudentSerializer implements MySerializer<Student> {
    public void output(RandomAccessFile out, Student val) throws IOException {
        out.writeInt(val.getGroupId());
        out.writeUTF(val.getName());
        out.writeUTF(val.getHometown());
        out.writeLong(val.getBirthDate().getTime());
        out.writeBoolean(val.isHasDormitory());
        out.writeDouble(val.getAverageScore());
    }

    public Student input(RandomAccessFile in) throws IOException {
        Student tmp = new Student(
                in.readInt(),
                in.readUTF(),
                in.readUTF(),
                new Date(in.readLong()),
                in.readBoolean(),
                in.readDouble()
        );
        return tmp;
    }
}
