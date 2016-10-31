package ru.mipt.java2016.homework.g597.grishutin.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;

class StudentKeySerializationStrategy implements SerializationStrategy<StudentKey> {
    private IntegerSerializationStrategy integerSerializationStrategy = IntegerSerializationStrategy.getInstance();
    private StringSerializationStrategy stringSerializationStrategy = StringSerializationStrategy.getInstance();

    @Override
    public void serialize(StudentKey studentKey, RandomAccessFile raf) throws IOException {
        integerSerializationStrategy.serialize(studentKey.getGroupId(), raf);
        stringSerializationStrategy.serialize(studentKey.getName(), raf);
    }

    @Override
    public StudentKey deserialize(RandomAccessFile raf) throws IOException {
        return new StudentKey(integerSerializationStrategy.deserialize(raf),
                stringSerializationStrategy.deserialize(raf));
    }
}
