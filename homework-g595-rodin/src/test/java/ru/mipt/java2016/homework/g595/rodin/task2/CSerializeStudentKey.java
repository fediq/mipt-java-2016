package ru.mipt.java2016.homework.g595.rodin.task2;

import ru.mipt.java2016.homework.g595.rodin.task2.Serializer.CSerializeInteger;
import ru.mipt.java2016.homework.g595.rodin.task2.Serializer.CSerializeString;
import ru.mipt.java2016.homework.g595.rodin.task2.Serializer.ISerialize;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.util.StringTokenizer;

/**
 * Created by Dmitry on 26.10.16.
 */
public class CSerializeStudentKey implements ISerialize<StudentKey> {
    @Override
    public String serialize(StudentKey argument) throws IllegalArgumentException {
        StringBuilder builder = new StringBuilder();
        return builder.append("\"groupId\":").append(argument.getGroupId()).append(",")
                .append("\"name\":").append(argument.getName()).toString();
    }

    @Override
    public StudentKey deserialize(String argument) throws IllegalArgumentException {
        StringTokenizer tokenizer = new StringTokenizer(argument, "\":,", false);
        Integer studentGroupId = new Integer(0);
        String studentName = new String();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.equals("groupId")) {
                token = tokenizer.nextToken();
                CSerializeInteger serializeInteger = new CSerializeInteger();
                studentGroupId = serializeInteger.deserialize(token);
                continue;
            }
            if (token.equals("name")) {
                token = tokenizer.nextToken();
                CSerializeString serializeString = new CSerializeString();
                studentName = serializeString.deserialize(token);
            }
        }
        return new StudentKey(studentGroupId, studentName);
    }

}
