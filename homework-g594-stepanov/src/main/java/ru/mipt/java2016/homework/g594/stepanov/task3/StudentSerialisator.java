package ru.mipt.java2016.homework.g594.stepanov.task3;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.util.Date;

public class StudentSerialisator extends ObjectSerialisator<Student> {
    @Override
    public String toString(Student object) {
        StringBuilder sb = new StringBuilder("");
        sb.append(object.getGroupId());
        sb.append(",");
        sb.append(object.getName());
        sb.append(",");
        sb.append(object.getHometown());
        sb.append(",");
        sb.append(object.getBirthDate().getTime());
        sb.append(",");
        sb.append(object.isHasDormitory());
        sb.append(",");
        sb.append(object.getAverageScore());
        sb.append("\n");
        return sb.toString();
    }

    @Override
    public Student toObject(String s) {
        Integer pos = s.indexOf(',');
        Integer groupId = Integer.parseInt(s.substring(0, pos));
        s = s.substring(pos + 1, s.length());
        pos = s.indexOf(',');
        String name = s.substring(0, pos);
        s = s.substring(pos + 1, s.length());
        pos = s.indexOf(',');
        String town = s.substring(0, pos);
        s = s.substring(pos + 1, s.length());
        pos = s.indexOf(',');
        Date date = new Date(Long.parseLong(s.substring(0, pos)));
        s = s.substring(pos + 1, s.length());
        pos = s.indexOf(',');
        Boolean dormitory = Boolean.parseBoolean(s.substring(0, pos));
        s = s.substring(pos + 1, s.length());
        Double score = Double.parseDouble(s);
        return new Student(groupId, name, town, date, dormitory, score);
    }
}
