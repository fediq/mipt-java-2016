package ru.mipt.java.homework.g595.zueva.task3;

import ru.mipt.java2016.homework.g595.zueva.task2.task3.OptKVStorageSerializer;
import ru.mipt.java2016.homework.g595.zueva.task2.task3.CombienedSerializer;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import java.nio.ByteBuffer;

public class OptKVStudentKeySerializer implements OptKVStorageSerializer<StudentKey> {

    @Override
    public int size(StudentKey value) {
        return Integer.SIZE / 8 + 2 * (value.getName().length() + 1);
    }

    @Override
    public ByteBuffer stringToStream(StudentKey value) {
        ByteBuffer serialized = ByteBuffer.allocate(size(value));
        serialized.putInt(value.getGroupId());
        CombienedSerializer StExample =
                new CombienedSerializer();
        serialized.put(StExample.stringToStream(value.getName()).array());
        return serialized;
    }

    @Override
    public StudentKey deserializationFromStream(ByteBuffer in) {
        CombienedSerializer StrStud =
                new CombienedSerializer();
        return new StudentKey(in.getInt(), StrStud.deserializationFromStream(in));
    }

    }

