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
public class StudentKeySerialization implements Serialization<StudentKey> {
    private static StudentKeySerialization instance = new StudentKeySerialization();

    private IntegerSerialization integerSerialization = IntegerSerialization.getInstance();
    private StringSerialization stringSerialization = StringSerialization.getInstance();

    public static StudentKeySerialization getInstance() {
        return instance;
    }

    private StudentKeySerialization() {
    }

    @Override
    public void write(RandomAccessFile file, StudentKey object) throws IOException {
        integerSerialization.write(file, object.getGroupId());
        stringSerialization.write(file, object.getName());
    }

    @Override
    public StudentKey read(RandomAccessFile file) throws IOException {
        int groupID = integerSerialization.read(file);
        String name = stringSerialization.read(file);
        return new StudentKey(groupID, name);
    }
}
