package ru.mipt.java2016.homework.g597.miller.task2;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Vova Miller on 31.10.2016.
 */
public class MillerStorageStudents extends MillerStorageAbstract<StudentKey, Student> {

    public MillerStorageStudents(String directoryName) {
        super(directoryName);
    }

    @Override
    protected StudentKey readKey() {
        try {
            int groupId = file.readInt();
            int n = file.readInt();
            StringBuilder name = new StringBuilder();
            if (n < 0) {
                throw new RuntimeException("Invalid storage file.");
            }
            for (int i = 0; i < n; ++i) {
                name.append(file.readChar());
            }
            return new StudentKey(groupId, name.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Student readValue() {
        try {
            int n;
            int groupId = file.readInt();
            n = file.readInt();
            StringBuilder name = new StringBuilder();
            if (n < 0) {
                throw new RuntimeException("Invalid storage file.");
            }
            for (int i = 0; i < n; ++i) {
                name.append(file.readChar());
            }
            StringBuilder hometown = new StringBuilder();
            n = file.readInt();
            if (n < 0) {
                throw new RuntimeException("Invalid storage file.");
            }
            for (int i = 0; i < n; ++i) {
                hometown.append(file.readChar());
            }
            long time = file.readLong();
            Date birthDate = new Date();
            birthDate.setTime(time);
            boolean hasDormitory = file.readBoolean();
            double averageScore = file.readDouble();
            return new Student(groupId, name.toString(), hometown.toString(), birthDate, hasDormitory, averageScore);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void writeKey(StudentKey key) {
        try {
            file.writeInt(key.getGroupId());
            file.writeInt(key.getName().length());
            file.writeChars(key.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void writeValue(Student value) {
        try {
            file.writeInt(value.getGroupId());
            file.writeInt(value.getName().length());
            file.writeChars(value.getName());
            file.writeInt(value.getHometown().length());
            file.writeChars(value.getHometown());
            file.writeLong(value.getBirthDate().getTime());
            file.writeBoolean(value.isHasDormitory());
            file.writeDouble(value.getAverageScore());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}