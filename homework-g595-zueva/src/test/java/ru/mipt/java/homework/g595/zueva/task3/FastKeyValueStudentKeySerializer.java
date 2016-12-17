package ru.mipt.java.homework.g595.zueva.task3;

import ru.mipt.java2016.homework.g595.zueva.task2.task3.OptKVStorageSerializer;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.nio.ByteBuffer;
import java.util.Date;
public class FastKeyValueStudentKeySerializer implements OptKVStorageSerializer<Student> {
    @Override
     public ByteBuffer srlzToStr(Student value) {
                ByteBuffer serialized = ByteBuffer.allocate(SrlzSize(value));
                FastStudentKeySerializer studentKeySerializer = new FastStudentKeySerializer();
                serialized.put(studentKeySerializer.serializeToStream(new StudentKey(value.getGroupId(),
                                value.getName())).array());
                FastStringSerializerForStudentKeyAndStudent fastStringSerializerForStudentKeyAndStudent =
                                new FastStringSerializerForStudentKeyAndStudent();
                serialized.put(fastStringSerializerForStudentKeyAndStudent.serializeToStream(value.getHometown()).array());
                serialized.putLong(value.getBirthDate().getTime());
                if (value.isHasDormitory()) {
                        serialized.put((byte) 1);
                    } else {
                        serialized.put((byte) 0);
                    }
                serialized.putDouble(value.getAverageScore());
                return serialized;
            }

             @Override
     public Student desrlzFrStr(ByteBuffer input) {
                FastStringSerializerForStudentKeyAndStudent fastStringSerializerForStudentKeyAndStudent =
                                new FastStringSerializerForStudentKeyAndStudent();
                return new Student(input.getInt(),
                                fastStringSerializerForStudentKeyAndStudent.deserializeFromStream(input),
                                fastStringSerializerForStudentKeyAndStudent.deserializeFromStream(input),
                                new Date(input.getLong()), (input.get() == 1), input.getDouble());
            }

             @Override
     public int SrlzSize(Student value) {
                return Integer.SIZE / 8 + 2 * (value.getName().length() + 1) +
                                2 * (value.getHometown().length() + 1) + Long.SIZE / 8 + 1 + Double.SIZE / 8;
            }
}
