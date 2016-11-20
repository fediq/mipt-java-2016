package ru.mipt.java2016.homework.g596.kozlova.task3;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

public class MyStudentKeySerialization implements MySerialization<StudentKey> {
    @Override
    public StudentKey read(String s) {
        String[] parts = s.split("/");
        int groupId = Integer.parseInt(parts[0]);
        String name = parts[1];
        return new StudentKey(groupId, name);
    }

    @Override
    public String write(StudentKey obj) {
        StringBuffer s = new StringBuffer();
        s.append(obj.getGroupId());
        s.append('/');
        s.append(obj.getName());
        return s.toString();
    }
}