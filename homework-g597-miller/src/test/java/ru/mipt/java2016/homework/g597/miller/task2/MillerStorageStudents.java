package ru.mipt.java2016.homework.g597.miller.task2;

import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Vova Miller on 31.10.2016.
 */
public class MillerStorageStudents extends MillerStorageAbstract<StudentKey, Student> {

    public MillerStorageStudents(String directoryName) throws IOException {
        super(directoryName);
    }

    @Override
    protected StudentKey readKey(RandomAccessFile file) throws IOException {
        try {
            int groupId = file.readInt();
            int n = file.readInt();
            if (n < 0) {
                throw new IOException("Invalid storage file.");
            }
            byte[] name = new byte[n];
            file.read(name, 0, n);
            return new StudentKey(groupId, new String(name, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected Student readValue(RandomAccessFile file) throws IOException {
        try {
            int n;
            int groupId = file.readInt();
            n = file.readInt();
            if (n < 0) {
                throw new IOException("Invalid storage file.");
            }
            byte[] name = new byte[n];
            file.read(name, 0, n);
            n = file.readInt();
            if (n < 0) {
                throw new IOException("Invalid storage file.");
            }
            byte[] hometown = new byte[n];
            file.read(hometown, 0, n);
            long time = file.readLong();
            Date birthDate = new Date();
            birthDate.setTime(time);
            boolean hasDormitory = file.readBoolean();
            double averageScore = file.readDouble();
            return new Student(groupId, new String(name, StandardCharsets.UTF_8),
                    new String(hometown, StandardCharsets.UTF_8), birthDate,
                    hasDormitory, averageScore);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected void writeKey(RandomAccessFile file, StudentKey key) throws IOException {
        try {
            file.writeInt(key.getGroupId());
            byte[] name = key.getName().getBytes();
            file.writeInt(name.length);
            file.write(name);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected void writeValue(RandomAccessFile file, Student value) throws IOException {
        try {
            // Group ID
            file.writeInt(value.getGroupId());
            // Name
            byte[] name = value.getName().getBytes();
            file.writeInt(name.length);
            file.write(name);
            // Hometown
            byte[] hometown = value.getHometown().getBytes();
            file.writeInt(hometown.length);
            file.write(hometown);
            // Birth Date
            file.writeLong(value.getBirthDate().getTime());
            // Has Dormitory
            file.writeBoolean(value.isHasDormitory());
            // Average Score
            file.writeDouble(value.getAverageScore());
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}