package ru.mipt.java2016.homework.g596.proskurina.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.util.Date;

/**
 * Created by Lenovo on 31.10.2016.
 */
public class SerialiseStudent implements SerialiserInterface<Student> {

    @Override
    public String serialise(Student inStudent) {
        StringBuffer outString = new StringBuffer();
        outString.append(inStudent.getGroupId())
                 .append(",")
                 .append(inStudent.getName())
                 .append(",")
                 .append(inStudent.getHometown())
                 .append(",")
                 .append(inStudent.getBirthDate().getTime())
                 .append(",")
                 .append(inStudent.isHasDormitory())
                 .append(",")
                 .append(inStudent.getAverageScore());
        return outString.toString();
    }

    @Override
    public Student deserialise(String inString) {
        String[] tokens = inString.split(",");
        if (tokens.length != 6) {
            throw new RuntimeException("Wrong input");
        }

        int groupId = Integer.parseInt(tokens[0]);
        String name = tokens[1];
        String hometown = tokens[2];
        Date birthDate = new Date(Long.parseLong(tokens[3]));
        boolean hasDormitory = Boolean.parseBoolean(tokens[4]);
        double averageScore = Double.parseDouble(tokens[5]);
        return new Student(groupId, name, hometown, birthDate, hasDormitory, averageScore);
    }

    @Override
    public String getType() {
        return "Student";
    }
}
