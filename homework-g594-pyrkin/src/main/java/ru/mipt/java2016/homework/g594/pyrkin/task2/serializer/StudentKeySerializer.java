package ru.mipt.java2016.homework.g594.pyrkin.task2.serializer;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.nio.ByteBuffer;

/**
 * StudentKeySerializer
 * Created by randan on 10/30/16.
 */
public class StudentKeySerializer implements SerializerInterface<StudentKey> {

    @Override
    public int sizeOfSerialize(StudentKey object) {
        return Integer.SIZE / 8 + 2 * (object.getName().length() + 1);
    }

    @Override
    public ByteBuffer serialize(StudentKey object) {
        ByteBuffer resultBuffer = ByteBuffer.allocate(sizeOfSerialize(object));

        resultBuffer.putInt(object.getGroupId());

        StringSerializer stringSerializer = new StringSerializer();
        resultBuffer.put(stringSerializer.serialize(object.getName()).array());

        return resultBuffer;
    }

    @Override
    public StudentKey deserialize(ByteBuffer inputBuffer) {
        int groupId;
        String name;

        groupId = inputBuffer.getInt();

        StringSerializer stringSerializer = new StringSerializer();
        name = stringSerializer.deserialize(inputBuffer);

        return new StudentKey(groupId, name);
    }
}
