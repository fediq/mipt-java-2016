package ru.mipt.java2016.homework.g594.kalinichenko.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Created by masya on 30.10.16.
 */
public class MyStudentSerializer extends MySerializer<Student> {
    @Override
    public Student get(FileInputStream in) {
        Integer groupID = getInt(in);
        String name = getStr(in);
        String hometown = getStr(in);
        Date birthDate = getDate(in);
        boolean hasDormitory = getBool(in);
        Double average = getDouble(in);
        return new Student(groupID, name, hometown, birthDate, hasDormitory, average);
    }

    @Override
    public void put(FileOutputStream out, Student student) {
        putInt(out, student.getGroupId());
        putStr(out, student.getName());
        putStr(out, student.getHometown());
        putDate(out, student.getBirthDate());
        putBool(out, student.isHasDormitory());
        putDouble(out, student.getAverageScore());
    }

    private Date getDate(FileInputStream in) {
        int len = Double.BYTES;
        byte[] data = new byte[len];
        try {
            if (in.read(data) == -1 || data.length != len) {
                throw new IllegalStateException("Wrong file");
            }
        } catch (Exception exc) {
            throw new IllegalStateException("Invalid work with file");
        }
        return new Date(ByteBuffer.wrap(data).getLong());
    }

    protected void putDate(FileOutputStream out, Date date) {
        Long val = date.getTime();
        ByteBuffer data = ByteBuffer.allocate(Long.BYTES);
        data.putLong(val);
        try {
            out.write(data.array());
        } catch (Exception exc) {
            throw new IllegalStateException("Invalid work with file");
        }
    }

    private boolean getBool(FileInputStream in) {
        int len = 1;
        byte[] data = new byte[len];
        try {
            if (in.read(data) == -1 || data.length != len) {
                throw new IllegalStateException("Wrong file");
            }
        } catch (Exception exc) {
            throw new IllegalStateException("Invalid work with file");
        }
        if (data[0] != 0 && data[0] != 1) {
            throw new IllegalStateException("Wrong data");
        }
        return data[0] == 1;
    }

    protected void putBool(FileOutputStream out, boolean val) {
        byte[] data = new byte[1];
        if (val) {
            data[0] = 1;
        }
        try {
            out.write(data);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid work with file");
        }
    }
}
