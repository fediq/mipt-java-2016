package ru.mipt.java2016.homework.g595.popovkin.task2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import ru.mipt.java2016.homework.tests.task2.Student;
import java.util.Date;

/**
 * Created by Howl on 30.10.2016.
 */
public class StudentParser implements ItemParser<Student> {

    @Override
    public void serialize(Student arg, FileOutputStream out) throws IOException {
        new IntegerParser().serialize(arg.getGroupId(), out);
        new StringParser().serialize(arg.getName(), out);
        new StringParser().serialize(arg.getHometown(), out);
        new DateParser().serialize(arg.getBirthDate(), out);
        new BooleanParser().serialize(arg.isHasDormitory(), out);
        new DoubleParser().serialize(arg.getAverageScore(), out);
    }

    @Override
    public Student deserialize(FileInputStream in) throws IOException {
        int GroupId = new IntegerParser().deserialize(in);
        String Name = new StringParser().deserialize(in);
        String Hometown = new StringParser().deserialize(in);
        Date BirthDate = new DateParser().deserialize(in);
        boolean Dormitory = new BooleanParser().deserialize(in);
        Double AverageScore = new DoubleParser().deserialize(in);
        return new Student(GroupId, Name, Hometown, BirthDate, Dormitory, AverageScore);
    }
}