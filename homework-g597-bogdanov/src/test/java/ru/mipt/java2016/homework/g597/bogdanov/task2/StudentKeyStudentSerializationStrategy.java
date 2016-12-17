package ru.mipt.java2016.homework.g597.bogdanov.task2;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

public class StudentKeyStudentSerializationStrategy implements SerializationStrategy<StudentKey, Student> {
    private static final StudentKeyStudentSerializationStrategy INSTANCE =
            new StudentKeyStudentSerializationStrategy();

    public static StudentKeyStudentSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private StudentKeyStudentSerializationStrategy() {
    }

    private void writeString(DataOutput file, String string) throws IOException {
        byte[] bytes = string.getBytes();
        file.writeInt(bytes.length);
        file.write(bytes);
    }

    private String readString(DataInput file) throws IOException {
        int length = file.readInt();
        byte[] bytes = new byte[length];
        file.readFully(bytes);
        return new String(bytes);
    }

    @Override
    public void writeKey(DataOutput file, StudentKey key) throws IOException {
        file.writeInt(key.getGroupId());
        writeString(file, key.getName());
    }

    @Override
    public void writeValue(DataOutput file, Student value) throws IOException {
        file.writeInt(value.getGroupId());
        writeString(file, value.getName());
        writeString(file, value.getHometown());
        file.writeLong(value.getBirthDate().getTime());
        file.writeBoolean(value.isHasDormitory());
        file.writeDouble(value.getAverageScore());
    }

    @Override
    public StudentKey readKey(DataInput file) throws IOException {
        int groupId = file.readInt();
        String name = readString(file);
        return new StudentKey(groupId, name);
    }

    @Override
    public Student readValue(DataInput file) throws IOException {
        int groupId = file.readInt();
        String name = readString(file);
        String homeTown = readString(file);
        Date birthDate = new Date(file.readLong());
        Boolean isHasDormitory = file.readBoolean();
        Double averageScore = file.readDouble();
        return new Student(groupId, name, homeTown, birthDate, isHasDormitory, averageScore);
    }
}
