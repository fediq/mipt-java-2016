package ru.mipt.java2016.homework.g594.pyrkin.task2.serializer;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.nio.ByteBuffer;
import java.util.Date;

/**
 * StudentSerializer
 * Created by randan on 10/30/16.
 */
public class StudentSerializer implements SerializerInterface<Student>{

    @Override
    public int sizeOfSerialize(Student object) {
        StudentKeySerializer studentKeySerializer = new StudentKeySerializer();
        return studentKeySerializer.sizeOfSerialize(new StudentKey(object.getGroupId(),
                                                                   object.getName())) +
               2 * (object.getHometown().length() + 1) + 8 + 1 + 8;
    }

    @Override
    public ByteBuffer serialize(Student object) {
        ByteBuffer resultBuffer = ByteBuffer.allocate(this.sizeOfSerialize(object));
        StudentKeySerializer studentKeySerializer = new StudentKeySerializer();
        resultBuffer.put(studentKeySerializer.serialize(new StudentKey(object.getGroupId(),
                                                                       object.getName())));
        for(char symbol : object.getHometown().toCharArray()){
            resultBuffer.putChar(symbol);
        }
        resultBuffer.putLong(object.getBirthDate().getTime());
        resultBuffer.put((byte)(object.isHasDormitory() ? 1 : 0));
        resultBuffer.putDouble(object.getAverageScore());
        return null;
    }

    @Override
    public Student deserialize(ByteBuffer inputBuffer) {
        StudentKey studentKey;
        StudentKeySerializer studentKeySerializer = new StudentKeySerializer();
        StringBuilder hometown = new StringBuilder();
        long time;
        boolean hasDormitory;
        double averageScore;
        studentKey = studentKeySerializer.deserialize(inputBuffer);
        char symbol;
        while ((symbol = inputBuffer.getChar()) != '\0'){
            hometown.append(symbol);
        }
        time = inputBuffer.getLong();
        hasDormitory = (inputBuffer.get() == 1);
        averageScore = inputBuffer.getDouble();
        return new Student(studentKey.getGroupId(), studentKey.getName(),
                           hometown.toString(), new Date(time), hasDormitory, averageScore);
    }
}
