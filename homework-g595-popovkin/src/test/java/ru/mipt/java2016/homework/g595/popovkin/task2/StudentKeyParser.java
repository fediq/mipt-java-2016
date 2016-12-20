package ru.mipt.java2016.homework.g595.popovkin.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.*;

/**
 * Created by Howl on 30.10.2016.
 */
public class StudentKeyParser implements ItemParser<StudentKey> {

    @Override
    public void serialize(StudentKey arg, OutputStream out) throws IOException {
        new IntegerParser().serialize(arg.getGroupId(), out);
        new StringParser().serialize(arg.getName(), out);
    }

    @Override
    public StudentKey deserialize(InputStream in) throws IOException {
        int groupId = new IntegerParser().deserialize(in);
        String name = new StringParser().deserialize(in);
        return new StudentKey(groupId, name);
    }
}