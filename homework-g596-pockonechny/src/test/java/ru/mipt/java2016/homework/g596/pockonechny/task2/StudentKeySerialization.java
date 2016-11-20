package ru.mipt.java2016.homework.g596.pockonechny.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by celidos on 30.10.16.
 */

public class StudentKeySerialization implements SerializationStrategy<StudentKey> {

    @Override
    public StudentKey read(DataInputStream readingDevice) throws IOException {
        return new StudentKey(readingDevice.readInt(), readingDevice.readUTF());
    }

    @Override
    public void write(DataOutputStream writingDevice, StudentKey obj) throws IOException {
        writingDevice.writeInt(obj.getGroupId());
        writingDevice.writeUTF(obj.getName());
    }

    @Override
    public String getType() {
        return "STUDENTKEY";
    }
}