package ru.mipt.java2016.homework.g595.novikov.task2;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.Date;

public abstract class MySerialization<T> {
    public abstract void serialize(RandomAccessFile file, T object) throws IOException;

    public abstract T deserialize(RandomAccessFile file) throws IOException;

    protected void serializeString(RandomAccessFile file, String str) throws IOException {
        byte[] strBytes = str.getBytes("UTF-16");
        file.writeInt(strBytes.length);
        file.write(strBytes);
    }

    protected String deserializeString(RandomAccessFile file) throws IOException {
        int strLength = file.readInt();
        byte[] strBytes = new byte[strLength];
        file.readFully(strBytes);
        return new String(strBytes, Charset.forName("UTF-16"));
    }

    protected void serializeInteger(RandomAccessFile file, Integer object) throws IOException {
        file.writeInt(object.intValue());
    }

    protected Integer deserializeInteger(RandomAccessFile file) throws IOException {
        return file.readInt();
    }

    protected void serializeDouble(RandomAccessFile file, Double object) throws IOException {
        file.writeDouble(object.doubleValue());
    }

    protected Double deserializeDouble(RandomAccessFile file) throws IOException {
        return file.readDouble();
    }

    protected void serializeBoolean(RandomAccessFile file, Boolean object) throws IOException {
        file.writeBoolean(object.booleanValue());
    }

    protected Boolean deserializeBoolean(RandomAccessFile file) throws IOException {
        return file.readBoolean();
    }

    protected void serializeDate(RandomAccessFile file, Date object) throws IOException {
        file.writeLong(object.getTime());
    }

    protected Date deserializeDate(RandomAccessFile file) throws IOException {
        return new Date(file.readLong());
    }

    protected void serializeStudent(RandomAccessFile file, Student object) throws IOException {
        serializeInteger(file, object.getGroupId());
        serializeString(file, object.getName());
        serializeString(file, object.getHometown());
        serializeDate(file, object.getBirthDate());
        serializeBoolean(file, object.isHasDormitory());
        serializeDouble(file, object.getAverageScore());
    }

    protected Student deserializeStudent(RandomAccessFile file) throws IOException {
        return new Student(deserializeInteger(file),
                deserializeString(file),
                deserializeString(file),
                deserializeDate(file),
                deserializeBoolean(file),
                deserializeDouble(file));
    }

    protected void serializeStudentKey(RandomAccessFile file, StudentKey object) throws IOException {
        serializeInteger(file, object.getGroupId());
        serializeString(file, object.getName());
    }

    protected StudentKey deserializeStudentKey(RandomAccessFile file) throws IOException {
        return new StudentKey(deserializeInteger(file),
                deserializeString(file));
    }
}
