package ru.mipt.java2016.homework.g597.dmitrieva.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Created by macbook on 30.10.16.
 */
public class StudentValueSerialization implements SerializationStrategy<Student> {
    @Override
    public Student read(RandomAccessFile file) throws IOException {
        try {
            int groupID = file.readInt();
            String name = file.readUTF();
            String hometown = file.readUTF();
            long date = file.readLong();
            Date birthDate = new Date(date);
            boolean hasDormitory = file.readBoolean();
            double averageScore = file.readDouble();

            Student value = new Student(groupID, name, hometown, birthDate, hasDormitory, averageScore);
            return value;
        } catch (IOException e) {
            throw new IOException("Couldn't read during the StudentValue deserialization");
        }
    }

    @Override
    public void write(RandomAccessFile file, Student value) throws IOException {
        try {
            file.writeInt(value.getGroupId());
            file.writeUTF(value.getName());
            file.writeUTF(value.getHometown());
            file.writeLong(value.getBirthDate().getTime());
            file.writeBoolean(value.isHasDormitory());
            file.writeDouble(value.getAverageScore());
        } catch (IOException e) {
            throw new IOException("Couldn't write during the StudentValue serialization");
        }
    }
}
