package ru.mipt.java2016.homework.g594.vishnyakova.task3;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.util.Date;

/**
 * Created by Nina on 18.11.16.
 */
public class StudentNewSerializationStrategy implements NewSerializationStrategy<Student> {

    @Override
    public String serialize(Student obj) {
        StringBuffer sb = new StringBuffer();
        sb.append(obj.getGroupId());
        sb.append('!');
        sb.append(obj.getName());
        sb.append('!');
        sb.append(obj.getHometown());
        sb.append('!');
        sb.append(obj.getBirthDate().getTime());
        sb.append('!');
        sb.append(obj.isHasDormitory());
        sb.append('!');
        sb.append(obj.getAverageScore());
        return sb.toString();
    }

    @Override
    public Student deserialize(String s) {
        String parts[] = s.split("!");
        int gId = Integer.parseInt(parts[0]);
        String name = parts[1];
        String homeT = parts[2];
        Date bD = new Date(Long.parseLong(parts[3]));
        boolean dorm = Boolean.parseBoolean(parts[4]);
        double avSc = Double.parseDouble(parts[5]);
        return new Student(gId, name, homeT, bD, dorm, avSc);
    }
}