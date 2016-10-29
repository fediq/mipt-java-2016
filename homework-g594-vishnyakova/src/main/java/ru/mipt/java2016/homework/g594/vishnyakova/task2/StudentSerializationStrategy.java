package ru.mipt.java2016.homework.g594.vishnyakova.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Nina on 28.10.16.
 */
public class StudentSerializationStrategy extends SerializationStrategy<Student> {

    @Override
    public Student read(DataInputStream rd) throws IOException {
        int gId = rd.readInt();
        String name = rd.readUTF();
        String homeT = rd.readUTF();
        Date bD = new Date(rd.readLong());
        boolean dorm = rd.readBoolean();
        double avSc = rd.readDouble();
        return new Student(gId, name, homeT, bD, dorm, avSc);
    }

    @Override
    public void write(DataOutputStream wr, Student obj) throws IOException {
        wr.writeInt(obj.getGroupId());
        wr.writeUTF(obj.getName());
        wr.writeUTF(obj.getHometown());
        wr.writeLong(obj.getBirthDate().getTime());
        wr.writeBoolean(obj.isHasDormitory());
        wr.writeDouble(obj.getAverageScore());
    }
}