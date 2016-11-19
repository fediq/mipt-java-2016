package ru.mipt.java2016.homework.g594.nevstruev.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created by Владислав on 31.10.2016.
 */
public class StudentSerialize implements Serialize<Student> {
    @Override
    public Student read(BufferedReader input) throws IOException {
        Integer groupId = Integer.parseInt(input.readLine());
        String name = input.readLine();
        String hometown = input.readLine();
        Date birthday = new Date(Long.parseLong(input.readLine()));
        boolean hasDormitory = Boolean.parseBoolean(input.readLine());
        double averageScore = Double.parseDouble(input.readLine());
        return new Student(groupId, name, hometown, birthday, hasDormitory, averageScore);
    }

    @Override
    public void write(PrintWriter output, Student object) {
        output.println(object.getGroupId());
        output.println(object.getName());
        output.println(object.getHometown());
        output.println(String.valueOf(object.getBirthDate().getTime()));
        output.println(String.valueOf(object.isHasDormitory()));
        output.println(String.valueOf(object.getAverageScore()));
    }
}
