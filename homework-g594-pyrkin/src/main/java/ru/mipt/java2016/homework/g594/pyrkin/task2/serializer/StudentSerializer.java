package ru.mipt.java2016.homework.g594.pyrkin.task2.serializer;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.nio.ByteBuffer;
import java.util.Date;

/**
 * StudentSerializer
 * Created by randan on 10/30/16.
 */
public class StudentSerializer implements SerializerInterface<Student> {

    @Override
    public int sizeOfSerialize(Student object) {
        StudentKeySerializer studentKeySerializer = new StudentKeySerializer();
        return studentKeySerializer.sizeOfSerialize(new StudentKey(object.getGroupId(),
                object.getName())) +
                2 * (object.getHometown().length() + 1) + Long.SIZE / 8 + 1 + Double.SIZE / 8;
    }

    @Override
    public ByteBuffer serialize(Student object) {
        ByteBuffer resultBuffer = ByteBuffer.allocate(sizeOfSerialize(object));

        StudentKeySerializer studentKeySerializer = new StudentKeySerializer();
        resultBuffer.put(studentKeySerializer.serialize(new StudentKey(object.getGroupId(),
                object.getName())).array());

        StringSerializer stringSerializer = new StringSerializer();
        resultBuffer.put(stringSerializer.serialize(object.getHometown()).array());

        resultBuffer.putLong(object.getBirthDate().getTime());
        resultBuffer.put((byte) (object.isHasDormitory() ? 1 : 0));
        resultBuffer.putDouble(object.getAverageScore());

        return resultBuffer;
    }

    @Override
    public Student deserialize(ByteBuffer inputBuffer) {
        StudentKey studentKey;
        String hometown;
        long time;
        boolean hasDormitory;
        double averageScore;

        StudentKeySerializer studentKeySerializer = new StudentKeySerializer();
        studentKey = studentKeySerializer.deserialize(inputBuffer);

        StringSerializer stringSerializer = new StringSerializer();
        hometown = stringSerializer.deserialize(inputBuffer);

        time = inputBuffer.getLong();
        hasDormitory = (inputBuffer.get() == 1);
        averageScore = inputBuffer.getDouble();

        return new Student(studentKey.getGroupId(), studentKey.getName(),
                hometown, new Date(time), hasDormitory, averageScore);
    }
}
