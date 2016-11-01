package ru.mipt.java2016.homework.g597.povarnitsyn.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;


/**
 * Created by Ivan on 30.10.2016.
 */
public class StudenSerialization implements SerializationInterface<Student> {
    IntegerSerialization intSerializer = new IntegerSerialization();
    StringSerialization strSerializer = new StringSerialization();
    DoubleSerialization doubSerializer = new DoubleSerialization();
    BooleanSerialization boolSerializer = new BooleanSerialization();
    DateSerialization dateSerializer = new DateSerialization();

    @Override
    public Student deserialize(BufferedReader input) throws IOException {
        Integer groupId = intSerializer.deserialize(input);
        String name = strSerializer.deserialize(input);
        String hometown = strSerializer.deserialize(input);
        Date birthday = new Date(Long.parseLong(input.readLine()));
        boolean hasDormitory = boolSerializer.deserialize(input);
        double averageScore = doubSerializer.deserialize(input);
        return new Student(groupId, name, hometown, birthday, hasDormitory, averageScore);
    }

    @Override
    public void serialize(PrintWriter output, Student object) throws IOException {
        intSerializer.serialize(output, object.getGroupId());
        strSerializer.serialize(output, object.getName());
        strSerializer.serialize(output, object.getHometown());
        output.println(String.valueOf(object.getBirthDate().getTime()));
        boolSerializer.serialize(output, object.isHasDormitory());
        doubSerializer.serialize(output, object.getAverageScore());
    }
}
