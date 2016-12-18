package ru.mipt.java2016.homework.g595.ferenets.task2;


import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

public class StudentSerializationStrategy implements SerializationStrategy<Student> {
    @Override
    public Student read(RandomAccessFile file) throws IOException {
        return new Student(file.readInt(), file.readUTF(), file.readUTF(),
                new Date(file.readLong()), file.readBoolean(), file.readDouble());
    }

    @Override
    public void write(RandomAccessFile file, Student value) throws IOException {
        file.writeInt(value.getGroupId());
        file.writeUTF(value.getName());
        file.writeUTF(value.getHometown());
        file.writeLong(value.getBirthDate().getTime());
        file.writeBoolean(value.isHasDormitory());
        file.writeDouble(value.getAverageScore());
    }
}
