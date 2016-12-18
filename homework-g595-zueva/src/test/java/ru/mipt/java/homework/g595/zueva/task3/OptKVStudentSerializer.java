package ru.mipt.java.homework.g595.zueva.task3;

import ru.mipt.java2016.homework.g595.zueva.task2.task3.OptKVStorageSerializer;
import ru.mipt.java2016.homework.g595.zueva.task2.task3.CombienedSerializer;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.nio.ByteBuffer;
import java.util.Date;

class OptKVStudentSerializer implements OptKVStorageSerializer<Student> {
    @Override
    public int Size(Student value) {
        return Integer.SIZE / 8 + 2 * (value.getName().length() + 1) +
                2 * (value.getHometown().length() + 1) + Long.SIZE / 8 + 1 + Double.SIZE / 8;
    }

    @Override
    public ByteBuffer SerialToStrm(Student value) {
        ByteBuffer serialized = ByteBuffer.allocate(Size(value));
        OptKVStorageSerializer studentKeySerializer = new OptKVStudentKeySerializer();
        serialized.put(studentKeySerializer.SerialToStrm(new StudentKey(value.getGroupId(),
                value.getName())).array());
        CombienedSerializer StudentSKey =
                new CombienedSerializer();
        serialized.put(StudentSKey.SerialToStrm(value.getHometown()).array());
        serialized.putLong(value.getBirthDate().getTime());
        if (value.isHasDormitory()) {
            serialized.put((byte) 1);
        } else {
            serialized.put((byte) 0);
        }
        serialized.putDouble(value.getAverageScore());
        return serialized;
    }

    ;


    @Override
    public Student DeserialFromStrm(ByteBuffer in) {
        CombienedSerializer MyExample =
                new CombienedSerializer();
        return new Student(in.getInt(),
                MyExample.DeserialFromStrm(in),
                MyExample.DeserialFromStrm(in),
                new Date(in.getLong()), (in.get() == 1), in.getDouble());
    }
}

