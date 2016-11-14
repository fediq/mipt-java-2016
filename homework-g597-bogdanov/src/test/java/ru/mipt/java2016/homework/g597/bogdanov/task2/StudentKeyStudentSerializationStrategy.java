package ru.mipt.java2016.homework.g597.bogdanov.task2;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

public class StudentKeyStudentSerializationStrategy implements SerializationStrategy<StudentKey, Student> {
    private static final StudentKeyStudentSerializationStrategy INSTANCE =
            new StudentKeyStudentSerializationStrategy();

    public static StudentKeyStudentSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private StudentKeyStudentSerializationStrategy() {
    }

    private void writeString(RandomAccessFile file, String string) throws IOException {
        byte[] bytes = string.getBytes();
        file.writeInt(bytes.length);
        file.write(bytes);
    }

    private String readString(RandomAccessFile file) throws IOException {
        int length = file.readInt();
        byte[] bytes = new byte[length];
        file.readFully(bytes);
        return new String(bytes);
    }

    @Override
    public void write(RandomAccessFile file, StudentKey key, Student value) throws IOException {
        file.writeInt(value.getGroupId());
        writeString(file, value.getName());
        writeString(file, value.getHometown());
        file.writeLong(value.getBirthDate().getTime());
        file.writeBoolean(value.isHasDormitory());
        file.writeDouble(value.getAverageScore());
    }

    @Override
    public StudentKey readKey(RandomAccessFile file) throws IOException {
        long startPos = file.getFilePointer();
        int groupId = file.readInt();
        String name = readString(file);
        file.seek(startPos);
        return new StudentKey(groupId, name);
    }

    @Override
    public Student readValue(RandomAccessFile file) throws IOException {
        int groupId = file.readInt();
        String name = readString(file);
        String homeTown = readString(file);
        Date birthDate = new Date(file.readLong());
        Boolean isHasDormitory = file.readBoolean();
        Double averageScore = file.readDouble();
        return new Student(groupId, name, homeTown, birthDate, isHasDormitory, averageScore);
    }
}
