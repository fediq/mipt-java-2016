package ru.mipt.java2016.homework.g595.shakhray.task2;

import ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Classes.IntegerSerialization;
import ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Classes.StringSerialization;
import ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Interface.StorageSerialization;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Vlad on 29/10/2016.
 */
public class StudentKeySerialization implements StorageSerialization<StudentKey> {

    /**
     * Required serializations of other types
     */
    private IntegerSerialization integerSerialization = IntegerSerialization.getSerialization();
    private StringSerialization stringSerialization = StringSerialization.getSerialization();

    /**
     * This class is a singleton
     */
    private static StudentKeySerialization serialization = new StudentKeySerialization();

    private StudentKeySerialization() { }

    public static StudentKeySerialization getSerialization() {
        return serialization;
    }

    @Override
    public void write(RandomAccessFile file, StudentKey object) throws IOException {
        try {
            integerSerialization.write(file, object.getGroupId());
            stringSerialization.write(file, object.getName());
        } catch (IOException e) {
            throw new IOException("Could not write to file.");
        }
    }

    @Override
    public StudentKey read(RandomAccessFile file) throws IOException {
        try {
            Integer groupId = integerSerialization.read(file);
            String name = stringSerialization.read(file);
            return new StudentKey(groupId, name);
        } catch (IOException e) {
            throw new IOException("Could not read from file.");
        }
    }
}
