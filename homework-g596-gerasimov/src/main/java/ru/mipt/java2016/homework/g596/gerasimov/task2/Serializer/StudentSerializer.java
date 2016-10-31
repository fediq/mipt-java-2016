package ru.mipt.java2016.homework.g596.gerasimov.task2.Serializer;

import java.nio.ByteBuffer;
import java.util.Date;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by geras-artem on 30.10.16.
 */

public class StudentSerializer implements ISerializer<Student> {
    @Override
    public int sizeOfSerialization(Student object) {
        return Integer.SIZE / 8 + 2 * (object.getName().length() + 1) + 2 * (
                object.getHometown().length() + 1) + Long.SIZE / 8 + 1 + Double.SIZE / 8;
    }

    @Override
    public ByteBuffer serialize(Student object) {
        ByteBuffer result = ByteBuffer.allocate(this.sizeOfSerialization(object));

        StudentKeySerializer studentKeySerializer = new StudentKeySerializer();
        result.put(studentKeySerializer
                .serialize(new StudentKey(object.getGroupId(), object.getName())).array());

        StringSerializer stringSerializer = new StringSerializer();
        result.put(stringSerializer.serialize(object.getHometown()).array());

        result.putLong(object.getBirthDate().getTime());
        result.put((byte) (object.isHasDormitory() ? 1 : 0));
        result.putDouble(object.getAverageScore());

        return result;
    }

    @Override
    public Student deserialize(ByteBuffer code) {
        StringSerializer stringSerializer = new StringSerializer();
        return new Student(code.getInt(), stringSerializer.deserialize(code),
                           stringSerializer.deserialize(code), new Date(code.getLong()),
                           !(code.get() == 0), code.getDouble());
    }
}
