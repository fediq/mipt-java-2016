package ru.mipt.java2016.homework.g596.grebenshchikova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by liza on 31.10.16.
 */
public class StudentKeySerializer implements MySerializerInterface<StudentKey> {
    private final IntegerSerializer integerSerializer = IntegerSerializer.getExample();
    private final StringSerializer stringSerializer = StringSerializer.getExample();

    @Override
    public void write(DataOutput output, StudentKey object) throws IOException {
        integerSerializer.write(output, object.getGroupId());
        stringSerializer.write(output, object.getName());
    }

    @Override
    public StudentKey read(DataInput input) throws IOException {
        int groupID = integerSerializer.read(input);
        String name = stringSerializer.read(input);
        return new StudentKey(groupID, name);
    }

    private static final StudentKeySerializer EXAMPLE = new StudentKeySerializer();

    public static StudentKeySerializer getExample() {
        return EXAMPLE;
    }

    private StudentKeySerializer() {
    }
}

