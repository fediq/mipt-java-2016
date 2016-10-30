package ru.mipt.java2016.homework.g595.popovkin.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Howl on 30.10.2016.
 */
public class StudentKeyParser implements ItemParser<StudentKey> {

    @Override
    public void serialize(StudentKey arg, FileOutputStream out) throws IOException {
        new IntegerParser().serialize(arg.getGroupId(), out);
        new StringParser().serialize(arg.getName(), out);
    }

    @Override
    public StudentKey deserialize(FileInputStream in) throws IOException {
        int GroupId = new IntegerParser().deserialize(in);
        String Name = new StringParser().deserialize(in);
        return new StudentKey(GroupId, Name);
    }
}