package ru.mipt.java2016.homework.g596.ivanova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by julia on 30.10.16.
 */
public final class StudentKeySerialisation implements Serialisation<StudentKey> {
    /**
     * Instance of current class.
     */
    private static StudentKeySerialisation instance = new StudentKeySerialisation();

    /**
     * We need instance of IntegerSerialisation here,
     * because we'll use it to serialise StudentKey.groupId.
     */
    private IntegerSerialisation integerSerialisation = IntegerSerialisation.getInstance();

    /**
     * We need instance of StringSerialisation here,
     * because we'll use it to serialise StudentKey.name.
     */
    private StringSerialisation stringSerialisation = StringSerialisation.getInstance();

    /**
     * Constructor for the class.
     */
    private StudentKeySerialisation() { }

    /**
     * @return instance of current class.
     */
    public static StudentKeySerialisation getInstance() {
        return instance;
    }

    @Override
    public StudentKey read(final DataInput file) throws IOException {
        Integer groupId = integerSerialisation.read(file);
        String name = stringSerialisation.read(file);
        return new StudentKey(groupId, name);
    }

    @Override
    public long write(final DataOutput file, final StudentKey object) throws IOException {
        long size = integerSerialisation.write(file, object.getGroupId());
        size += stringSerialisation.write(file, object.getName());
        return size;
    }
}
