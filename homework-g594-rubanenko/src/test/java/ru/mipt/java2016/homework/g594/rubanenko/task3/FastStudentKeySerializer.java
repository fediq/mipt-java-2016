package ru.mipt.java2016.homework.g594.rubanenko.task3;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.nio.ByteBuffer;

/**
 * Created by king on 17.11.16.
 */

public class FastStudentKeySerializer implements FastKeyValueStorageSerializer<StudentKey> {
    @Override
    public ByteBuffer serializeToStream(StudentKey value) {
        ByteBuffer serialized = ByteBuffer.allocate(serializeSize(value));
        serialized.putInt(value.getGroupId());
        FastStringSerializerForStudentKeyAndStudent fastStringSerializerForStudentKeyAndStudent =
                new FastStringSerializerForStudentKeyAndStudent();
        serialized.put(fastStringSerializerForStudentKeyAndStudent.serializeToStream(value.getName()).array());
        return serialized;
    }

    @Override
    public StudentKey deserializeFromStream(ByteBuffer input) {
        FastStringSerializerForStudentKeyAndStudent fastStringSerializerForStudentKeyAndStudent =
                new FastStringSerializerForStudentKeyAndStudent();
        return new StudentKey(input.getInt(), fastStringSerializerForStudentKeyAndStudent.deserializeFromStream(input));
    }

    @Override
    public int serializeSize(StudentKey value) {
        return Integer.SIZE / 8 + 2 * (value.getName().length() + 1);
    }
}
