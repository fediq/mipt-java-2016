package ru.mipt.java2016.homework.g594.stepanov.task3;


import ru.mipt.java2016.homework.tests.task2.StudentKey;

public class StudentKeySerialisator extends ObjectSerialisator<StudentKey> {
    @Override
    public String toString(StudentKey object) {
        StringBuilder sb = new StringBuilder("");
        sb.append(object.getGroupId());
        sb.append(",");
        sb.append(object.getName());
        sb.append("\n");
        return sb.toString();
    }

    @Override
    public StudentKey toObject(String s) {
        int pos = s.indexOf(',');
        Integer groupId;
        groupId = Integer.parseInt(s.substring(0, pos));
        String name = s.substring(pos + 1, s.length());
        return new StudentKey(groupId, name);
    }
}
