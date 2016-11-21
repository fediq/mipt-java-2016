package ru.mipt.java2016.homework.g594.vorobeyv.task3;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Created by Morell on 30.10.2016.
 */
public class SerStVal extends OPSerializator<Student> {
    @Override
    public Student read(BufferedInputStream input) throws IOException {
        input.read(size.array());
        ByteBuffer buff = ByteBuffer.allocate(size.getInt());
        input.read(buff.array());
        buff.position(0);
        int groupId = buff.getInt();
        char c;
        StringBuilder str = new StringBuilder();
        while ((c = buff.getChar()) != '\0') {
            str.append(c);
        }
        StringBuilder hometown = new StringBuilder();
        while ((c = buff.getChar()) != '\0') {
            hometown.append(c);
        }
        long birthDate = buff.getLong();
        boolean hasDormitory = (buff.get() == 1);
        double averageScore = buff.getDouble();
        return new Student(groupId, str.toString(), hometown.toString(),
                new Date(birthDate), hasDormitory, averageScore);
    }

    @Override
    public int write(BufferedOutputStream output, Student value) throws IOException {
        int intSize = Integer.SIZE / 8 + 2 * (value.getName().length() + 1) +
                2 * (value.getHometown().length() + 1) + Long.SIZE / 8 + 1 + Double.SIZE / 8;
        size.putInt(0, intSize);
        ByteBuffer buff = ByteBuffer.allocate(intSize);
        buff.putInt(value.getGroupId());
        for (char c : value.getName().toCharArray()) {
            buff.putChar(c);
        }
        buff.putChar('\0');
        for (char c : value.getHometown().toCharArray()) {
            buff.putChar(c);
        }
        buff.putChar('\0');
        buff.putLong(value.getBirthDate().getTime());
        buff.put((byte) (value.isHasDormitory() ? 1 : 0));
        buff.putDouble(value.getAverageScore());
        output.write(size.array());
        output.write(buff.array());

        return intSize;
    }

    @Override
    public Student randRead(RandomAccessFile input, long offset) throws IOException {
        input.seek(offset);
        int sizeByte = input.readInt();
        ByteBuffer buff = ByteBuffer.allocate(sizeByte);
        input.read(buff.array());
        buff.position(0);
        int groupId = buff.getInt();
        char c;
        StringBuilder str = new StringBuilder();
        while ((c = buff.getChar()) != '\0') {
            str.append(c);
        }
        StringBuilder hometown = new StringBuilder();
        while ((c = buff.getChar()) != '\0') {
            hometown.append(c);
        }
        long birthDate = buff.getLong();
        boolean hasDormitory = (buff.get() == 1);
        double averageScore = buff.getDouble();
        return new Student(groupId, str.toString(), hometown.toString(),
                new Date(birthDate), hasDormitory, averageScore);
    }


}
