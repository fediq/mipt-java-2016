package ru.mipt.java2016.homework.g596.pockonechny.task3;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.*;

/**
 * Created by celidos on 30.10.16.
 */

public class StudentKeySerialization implements SerializationStrategy<StudentKey> {

    @Override
    public StudentKey read(DataInput readingDevice) throws IOException {
        return new StudentKey(readingDevice.readInt(), readingDevice.readUTF());
    }

    @Override
    public void write(DataOutput writingDevice, StudentKey obj) throws IOException {
        writingDevice.writeInt(obj.getGroupId());
        writingDevice.writeUTF(obj.getName());
    }

    @Override
    public String getType() {
        return "STUDENTKEY";
    }
}