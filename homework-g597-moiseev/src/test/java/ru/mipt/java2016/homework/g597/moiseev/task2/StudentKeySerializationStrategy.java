package ru.mipt.java2016.homework.g597.moiseev.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Стратегия сериализации для StudentKey
 *
 * @author Fedor Moiseev
 * @since 26.10.2016
 */
public class StudentKeySerializationStrategy implements SerializationStrategy<StudentKey> {
    private static final StudentKeySerializationStrategy INSTANCE = new StudentKeySerializationStrategy();

    private final IntegerSerializationStrategy integerSerializationStrategy =
            IntegerSerializationStrategy.getInstance();
    private final StringSerializationStrategy stringSerializationStrategy =
            StringSerializationStrategy.getInstance();

    public static StudentKeySerializationStrategy getInstance() {
        return INSTANCE;
    }

    private StudentKeySerializationStrategy() {
    }

    @Override
    public void write(RandomAccessFile file, StudentKey object) throws IOException {
        integerSerializationStrategy.write(file, object.getGroupId());
        stringSerializationStrategy.write(file, object.getName());
    }

    @Override
    public StudentKey read(RandomAccessFile file) throws IOException {
        int groupID = integerSerializationStrategy.read(file);
        String name = stringSerializationStrategy.read(file);
        return new StudentKey(groupID, name);
    }
}
