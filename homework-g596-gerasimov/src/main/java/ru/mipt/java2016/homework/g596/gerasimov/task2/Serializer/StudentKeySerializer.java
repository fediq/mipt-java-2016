package ru.mipt.java2016.homework.g596.gerasimov.task2.Serializer;

import java.nio.ByteBuffer;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by geras-artem on 30.10.16.
 */

public class StudentKeySerializer implements ISerializer<StudentKey> {
    @Override
    public int sizeOfSerialization(StudentKey object) {
        return Integer.SIZE / 8 + 2 * (object.getName().length() + 1);
    }

    @Override
    public ByteBuffer serialize(StudentKey object) {
        ByteBuffer result = ByteBuffer.allocate(this.sizeOfSerialization(object));
        result.putInt(object.getGroupId());
        StringSerializer nameSerializer = new StringSerializer();
        result.put(nameSerializer.serialize(object.getName()).array());
        return result;
    }

    @Override
    public StudentKey deserialize(ByteBuffer code) {
        StringSerializer deserializeName = new StringSerializer();
        return new StudentKey(code.getInt(), deserializeName.deserialize(code));
    }
}
