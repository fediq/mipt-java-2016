package ru.mipt.java2016.homework.g596.ivanova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;
import ru.mipt.java2016.homework.tests.task2.Student;

/**
 * Created by julia on 30.10.16.
 */
public final class StudentSerialisation implements Serialisation<Student> {
    /**
     * Instance of current class.
     */
    private static StudentSerialisation instance = new StudentSerialisation();

    /**
     * We need instance of IntegerSerialisation here,
     * because we'll use it to serialise Student.groupId.
     */
    private IntegerSerialisation integerSerialisation = IntegerSerialisation.getInstance();

    /**
     * We need instance of StringSerialisation here,
     * because we'll use it to serialise Student.name and Student.hometown.
     */
    private StringSerialisation stringSerialisation = StringSerialisation.getInstance();

    /**
     * We need instance of DateSerialisation here,
     * because we'll use it to serialise Student.birthDate.
     */
    private DateSerialisation dateSerialisation = DateSerialisation.getInstance();

    /**
     * We need instance of BooleanSerialisation here,
     * because we'll use it to serialise Student.hasDormitory.
     */
    private BooleanSerialisation booleanSerialisation = BooleanSerialisation.getInstance();

    /**
     * We need instance of DoubleSerialisation here,
     * because we'll use it to serialise Student.averageScore.
     */
    private  DoubleSerialisation doubleSerialisation = DoubleSerialisation.getInstance();

    /**
     * Constructor for the class.
     */
    private StudentSerialisation() { }

    /**
     * @return instance of current class.
     */
    public static StudentSerialisation getInstance() {
        return instance;
    }

    @Override
    public Student read(final DataInput file) throws IOException {
        Integer groupId = integerSerialisation.read(file);
        String name = stringSerialisation.read(file);
        String hometown = stringSerialisation.read(file);
        Date birthDate = dateSerialisation.read(file);
        Boolean hasDormitory = booleanSerialisation.read(file);
        Double averageScore = doubleSerialisation.read(file);
        return new Student(groupId, name, hometown, birthDate, hasDormitory, averageScore);
    }

    @Override
    public long write(final DataOutput file, final Student object) throws IOException {
        long size = integerSerialisation.write(file, object.getGroupId());
        size += stringSerialisation.write(file, object.getName());
        size += stringSerialisation.write(file, object.getHometown());
        size += dateSerialisation.write(file, object.getBirthDate());
        size += booleanSerialisation.write(file, object.isHasDormitory());
        size += doubleSerialisation.write(file, object.getAverageScore());
        return size;
    }
}
