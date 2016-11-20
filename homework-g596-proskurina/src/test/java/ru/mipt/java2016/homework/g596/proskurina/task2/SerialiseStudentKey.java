package ru.mipt.java2016.homework.g596.proskurina.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by Lenovo on 31.10.2016.
 */
public class SerialiseStudentKey implements SerialiserInterface<StudentKey> {
    @Override
    public String serialise(StudentKey inStudentKey) {
        StringBuffer outString = new StringBuffer();
        outString.append(inStudentKey.getGroupId())
                 .append(",")
                 .append(inStudentKey.getName());
        return outString.toString();
    }

    @Override
    public StudentKey deserialise(String inString) {
        String[] tokens = inString.split(",");
        if (tokens.length != 2) {
            throw new RuntimeException("Wrong input");
        }

        int groupId = Integer.parseInt(tokens[0]);
        String name = tokens[1];
        return new StudentKey(groupId, name);
    }

    @Override
    public String getType() {
        return "StudentKey";
    }

}
