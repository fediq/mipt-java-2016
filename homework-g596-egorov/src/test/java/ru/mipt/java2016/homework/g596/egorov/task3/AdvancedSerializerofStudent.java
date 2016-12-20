package ru.mipt.java2016.homework.g596.egorov.task3;

/**
 * Created by евгений on 30.10.2016.
 */


import ru.mipt.java2016.homework.g596.egorov.task3.serializers.AdvancedSerializerInterface;
import ru.mipt.java2016.homework.tests.task2.Student;

import java.util.Date;

public class AdvancedSerializerofStudent implements AdvancedSerializerInterface<Student> {

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
