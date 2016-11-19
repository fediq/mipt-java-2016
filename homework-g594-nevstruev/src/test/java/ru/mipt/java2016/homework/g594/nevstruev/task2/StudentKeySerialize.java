package ru.mipt.java2016.homework.g594.nevstruev.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Владислав on 31.10.2016.
 */
public class StudentKeySerialize implements Serialize<StudentKey> {
    @Override
    public StudentKey read(BufferedReader input) throws IOException {
        Integer groupId = Integer.parseInt(input.readLine());
        String name = input.readLine();
        return new StudentKey(groupId, name);
    }

    @Override
    public void write(PrintWriter output, StudentKey object) {
        Integer groupId = object.getGroupId();
        output.println(groupId.toString());
        output.println(object.getName());
    }
}
