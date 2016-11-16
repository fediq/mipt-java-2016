package ru.mipt.java2016.homework.g595.popovkin.task2;

import java.io.*;

import ru.mipt.java2016.homework.tests.task2.Student;
import java.util.Date;

/**
 * Created by Howl on 30.10.2016.
 */
public class StudentParser implements ItemParser<Student> {

    @Override
    public void serialize(Student arg, OutputStream out) throws IOException {
        new IntegerParser().serialize(arg.getGroupId(), out);
        new StringParser().serialize(arg.getName(), out);
        new StringParser().serialize(arg.getHometown(), out);
        new DateParser().serialize(arg.getBirthDate(), out);
        new BooleanParser().serialize(arg.isHasDormitory(), out);
        new DoubleParser().serialize(arg.getAverageScore(), out);
    }

    @Override
    public Student deserialize(InputStream in) throws IOException {
        int groupId = new IntegerParser().deserialize(in);
        String name = new StringParser().deserialize(in);
        String hometown = new StringParser().deserialize(in);
        Date birthDate = new DateParser().deserialize(in);
        boolean dormitory = new BooleanParser().deserialize(in);
        Double averageScore = new DoubleParser().deserialize(in);
        return new Student(groupId, name, hometown, birthDate, dormitory, averageScore);
    }
}