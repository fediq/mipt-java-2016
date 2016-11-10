package ru.mipt.java2016.homework.g596.pockonechny.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by celidos on 30.10.16.
 */
public class StudentSerialization implements SerializationStrategy<Student> {

    @Override
    public Student read(DataInputStream readingDevice) throws IOException {
        return new Student(readingDevice.readInt(), readingDevice.readUTF(), readingDevice.readUTF(),
                new Date(readingDevice.readLong()), readingDevice.readBoolean(), readingDevice.readDouble());
    }

    @Override
    public void write(DataOutputStream writingDevice, Student obj) throws IOException {
        writingDevice.writeInt(obj.getGroupId());
        writingDevice.writeUTF(obj.getName());
        writingDevice.writeUTF(obj.getHometown());
        writingDevice.writeLong(obj.getBirthDate().getTime());
        writingDevice.writeBoolean(obj.isHasDormitory());
        writingDevice.writeDouble(obj.getAverageScore());
    }

    @Override
    public String getType() {
        return "STUDENT";
    }
}
