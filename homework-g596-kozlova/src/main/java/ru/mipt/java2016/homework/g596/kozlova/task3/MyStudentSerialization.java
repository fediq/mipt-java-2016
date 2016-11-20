package ru.mipt.java2016.homework.g596.kozlova.task3;

import ru.mipt.java2016.homework.tests.task2.Student;
import java.util.Date;

public class MyStudentSerialization implements MySerialization<Student> {
    @Override
    public String write(Student obj) {
        StringBuffer s = new StringBuffer();
        s.append(obj.getGroupId());
        s.append('/');
        s.append(obj.getName());
        s.append('/');
        s.append(obj.getHometown());
        s.append('/');
        s.append(obj.getBirthDate().getTime());
        s.append('/');
        s.append(obj.isHasDormitory());
        s.append('/');
        s.append(obj.getAverageScore());
        return s.toString();
    }

    @Override
    public Student read(String s) {
        String parts[] = s.split("/");
        int groupId = Integer.parseInt(parts[0]);
        String name = parts[1];
        String homeTown = parts[2];
        Date birthDate = new Date(Long.parseLong(parts[3]));
        boolean hasDormitory = Boolean.parseBoolean(parts[4]);
        double averageScore = Double.parseDouble(parts[5]);
        return new Student(groupId, name, homeTown, birthDate, hasDormitory, averageScore);
    }
}
