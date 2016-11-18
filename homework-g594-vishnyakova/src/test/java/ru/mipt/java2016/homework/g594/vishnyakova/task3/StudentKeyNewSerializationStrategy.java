package ru.mipt.java2016.homework.g594.vishnyakova.task3;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by Nina on 18.11.16.
 */
public class StudentKeyNewSerializationStrategy implements NewSerializationStrategy<StudentKey> {

    @Override
    public StudentKey deserialize(String s) {
        String parts[] = s.split("!");
        int grId = Integer.parseInt(parts[0]);
        String stName = parts[1];
        return new StudentKey(grId, stName);
    }

    @Override
    public String serialize(StudentKey obj) {
        StringBuffer sb = new StringBuffer();
        sb.append(obj.getGroupId());
        sb.append('!');
        sb.append(obj.getName());
        return sb.toString();
    }
}
