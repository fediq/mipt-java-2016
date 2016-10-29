package ru.mipt.java2016.homework.g595.novikov.task2;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

public abstract class MySerialization<T> {
    public abstract void serialize(DataOutput file, T object) throws IOException;

    public abstract T deserialize(DataInput file) throws IOException;

    protected void serializeString(DataOutput file, String str) throws IOException {
        byte[] strBytes = str.getBytes("UTF-16");
        file.writeInt(strBytes.length);
        file.write(strBytes);
    }

    protected String deserializeString(DataInput file) throws IOException {
        int strLength = file.readInt();
        byte[] strBytes = new byte[strLength];
        file.readFully(strBytes);
        return new String(strBytes, Charset.forName("UTF-16"));
    }

    protected void serializeInteger(DataOutput file, Integer object) throws IOException {
        file.writeInt(object.intValue());
    }

    protected Integer deserializeInteger(DataInput file) throws IOException {
        return file.readInt();
    }

    protected void serializeDouble(DataOutput file, Double object) throws IOException {
        file.writeDouble(object.doubleValue());
    }

    protected Double deserializeDouble(DataInput file) throws IOException {
        return file.readDouble();
    }

    protected void serializeBoolean(DataOutput file, Boolean object) throws IOException {
        file.writeBoolean(object.booleanValue());
    }

    protected Boolean deserializeBoolean(DataInput file) throws IOException {
        return file.readBoolean();
    }

    protected void serializeDate(DataOutput file, Date object) throws IOException {
        file.writeLong(object.getTime());
    }

    protected Date deserializeDate(DataInput file) throws IOException {
        return new Date(file.readLong());
    }

    protected void serializeStudent(DataOutput file, Student object) throws IOException {
        serializeInteger(file, object.getGroupId());
        serializeString(file, object.getName());
        serializeString(file, object.getHometown());
        serializeDate(file, object.getBirthDate());
        serializeBoolean(file, object.isHasDormitory());
        serializeDouble(file, object.getAverageScore());
    }

    protected Student deserializeStudent(DataInput file) throws IOException {
        return new Student(deserializeInteger(file),
                deserializeString(file),
                deserializeString(file),
                deserializeDate(file),
                deserializeBoolean(file),
                deserializeDouble(file));
    }

    protected void serializeStudentKey(DataOutput file, StudentKey object) throws IOException {
        serializeInteger(file, object.getGroupId());
        serializeString(file, object.getName());
    }

    protected StudentKey deserializeStudentKey(DataInput file) throws IOException {
        return new StudentKey(deserializeInteger(file),
                deserializeString(file));
    }
}
