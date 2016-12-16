package ru.mipt.java2016.homework.g595.proskurin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ru.mipt.java2016.homework.tests.task2.Student;
import java.sql.Date;

public class StudentSerializer implements MySerializer<Student> {
    public void output(DataOutputStream out, Student val) throws IOException {
        out.writeInt(val.getGroupId());
        out.writeUTF(val.getName());
        out.writeUTF(val.getHometown());
        out.writeLong(val.getBirthDate().getTime());
        out.writeBoolean(val.isHasDormitory());
        out.writeDouble(val.getAverageScore());
    }

    public Student input(DataInputStream in) throws IOException {
        Student tmp = new Student(
                in.readInt(),
                in.readUTF(),
                in.readUTF(),
                new Date(in.readLong()),
                in.readBoolean(),
                in.readDouble()
        );
        return tmp;
    }
}
