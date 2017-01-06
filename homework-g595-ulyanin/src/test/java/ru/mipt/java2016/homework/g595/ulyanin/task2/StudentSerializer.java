package ru.mipt.java2016.homework.g595.ulyanin.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

/**
 * @author ulyanin
 * @since 31.10.16
 */
public class StudentSerializer implements Serializer<Student> {
    private static StudentSerializer ourInstance = new StudentSerializer();

    public static StudentSerializer getInstance() {
        return ourInstance;
    }

    private StudentSerializer() { }

    @Override
    public void serialize(Student data, DataOutput dataOutputStream) throws IOException {
        IntegerSerializer.getInstance().serialize(data.getGroupId(), dataOutputStream);
        StringSerializer.getInstance().serialize(data.getName(), dataOutputStream);
        StringSerializer.getInstance().serialize(data.getHometown(), dataOutputStream);
        DateSerializer.getInstance().serialize(data.getBirthDate(), dataOutputStream);
        BooleanSerializer.getInstance().serialize(data.isHasDormitory(), dataOutputStream);
        DoubleSerializer.getInstance().serialize(data.getAverageScore(), dataOutputStream);
    }

    @Override
    public Student deserialize(DataInput dataInputStream) throws IOException {
        Integer groupId = IntegerSerializer.getInstance().deserialize(dataInputStream);
        String name = StringSerializer.getInstance().deserialize(dataInputStream);
        String homeTown = StringSerializer.getInstance().deserialize(dataInputStream);
        Date birthDate = DateSerializer.getInstance().deserialize(dataInputStream);
        Boolean hasDormitory = BooleanSerializer.getInstance().deserialize(dataInputStream);
        Double avgScore = DoubleSerializer.getInstance().deserialize(dataInputStream);
        return new Student(groupId, name, homeTown, birthDate, hasDormitory, avgScore);
    }
}
