package ru.mipt.java2016.homework.g597.povarnitsyn.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Ivan on 30.10.2016.
 */
public class StudentKeySerialization  implements SerializationInterface<StudentKey> {
    private IntegerSerialization intSerializer = new IntegerSerialization();
    private StringSerialization strSerializer = new StringSerialization();

    @Override
    public StudentKey deserialize(BufferedReader input) throws IOException {
        Integer groupId = intSerializer.deserialize(input);
        String name = strSerializer.deserialize(input);
        return new StudentKey(groupId, name);
    }

    @Override
    public void serialize(PrintWriter output, StudentKey object) throws IOException {
        intSerializer.serialize(output, object.getGroupId());
        strSerializer.serialize(output, object.getName());
    }
}
