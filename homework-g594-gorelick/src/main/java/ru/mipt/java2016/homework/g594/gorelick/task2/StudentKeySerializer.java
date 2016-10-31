package ru.mipt.java2016.homework.g594.gorelick.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.nio.ByteBuffer;
import java.sql.Date;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.g594.gorelick.task2.IntegerSerializer;

/**
 * Created by alex on 10/31/16.
 */

public class StudentKeySerializer implements Serializer<StudentKey> {
    @Override
    public ByteBuffer serialize(StudentKey object) {
        IntegerSerializer int_serialize = new IntegerSerializer();
        StringSerializer str_serialize = new StringSerializer();
        ByteBuffer group_id = int_serialize.serialize(object.getGroupId());
        ByteBuffer name = str_serialize.serialize(object.getName());
        ByteBuffer result = ByteBuffer.allocate(group_id.capacity() + name.capacity());
        result.put(group_id.array());
        result.put(name.array());
        return result;
    }
    @Override
    public StudentKey deserialize(ByteBuffer array) {
        IntegerSerializer int_deserialize = new IntegerSerializer();
        StringSerializer str_deserialize = new StringSerializer();
        Integer group_id = int_deserialize.deserialize(array);
        String name = str_deserialize.deserialize(array);
        return new StudentKey(group_id, name);
    }
}
