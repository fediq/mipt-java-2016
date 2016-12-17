package ru.mipt.java.homework.g595.zueva.task3;

import ru.mipt.java2016.homework.g595.zueva.task2.task3.OptKVStorageSerializer;
import ru.mipt.java2016.homework.g595.zueva.task2.task3.OptStrSerializerStudentStudentKey;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.nio.ByteBuffer;

public class FastKeyValueStudentSerializer implements OptKVStorageSerializer<Student> {
    @Override
    public StudentKey desrlzFrStr(ByteBuffer input) {
        OptStrSerializerStudentStudentKey fastStringSerializerForStudentKeyAndStudent =
                new OptStrSerializerStudentStudentKey();
        return new StudentKey(input.getInt(), fastStringSerializerForStudentKeyAndStudent.desrlzFrStr(input));
    }
    @Override
    public int SrlzSize(Student value) {
        return Integer.SIZE / 8 + 2 * (value.getName().length() + 1);
    }

    @Override
    public ByteBuffer srlzToStr(Student value) {
        ByteBuffer serialized = ByteBuffer.allocate(SrlzSize(value));
        serialized.putInt(value.getGroupId());
        OptStrSerializerStudentStudentKey fastStringSerializerForStudentKeyAndStudent =
                new OptStrSerializerStudentStudentKey();
        serialized.put(fastStringSerializerForStudentKeyAndStudent.srlzToStr(value.getName()).array());
        return serialized;
    }




}
