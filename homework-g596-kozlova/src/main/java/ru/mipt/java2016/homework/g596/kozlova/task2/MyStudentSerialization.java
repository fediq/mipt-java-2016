package ru.mipt.java2016.homework.g596.kozlova.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public class MyStudentSerialization extends MySerialization<Student> {

    @Override
    public Student read(DataInputStream read_from_file) throws IOException {
        int groupId = read_from_file.readInt();
        String name = read_from_file.readUTF();
        String home_town = read_from_file.readUTF();
        Date birth_date = new Date(read_from_file.readLong());
        boolean has_dormitory = read_from_file.readBoolean();
        double average_score = read_from_file.readDouble();
        return new Student(groupId, name, home_town, birth_date, has_dormitory, average_score);
    }

    @Override
    public void write(DataOutputStream write_to_file, Student student) throws IOException {
        write_to_file.writeInt(student.getGroupId());
        write_to_file.writeUTF(student.getName());
        write_to_file.writeUTF(student.getHometown());
        write_to_file.writeLong(student.getBirthDate().getTime());
        write_to_file.writeBoolean(student.isHasDormitory());
        write_to_file.writeDouble(student.getAverageScore());
    }
}