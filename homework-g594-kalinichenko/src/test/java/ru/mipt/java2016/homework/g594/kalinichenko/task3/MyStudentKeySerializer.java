package ru.mipt.java2016.homework.g594.kalinichenko.task3;


import ru.mipt.java2016.homework.g594.kalinichenko.task3.MySerializer;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

/**
 * Created by masya on 30.10.16.
 */

public class MyStudentKeySerializer extends MySerializer<StudentKey> {
    @Override
    public StudentKey get(RandomAccessFile in) {
        Integer groupID = getInt(in);
        String name = getStr(in);
        return new StudentKey(groupID, name);
    }

    @Override
    public void put(FileOutputStream out, StudentKey student) {
        putInt(out, student.getGroupId());
        putStr(out, student.getName());
    }
}


