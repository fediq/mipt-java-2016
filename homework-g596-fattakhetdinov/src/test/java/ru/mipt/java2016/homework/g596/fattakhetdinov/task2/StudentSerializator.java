package ru.mipt.java2016.homework.g596.fattakhetdinov.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;
import ru.mipt.java2016.homework.tests.task2.Student;

public class StudentSerializator implements SerializationStrategy<Student> {
    @Override
    public void serializeToFile(Student student, DataOutput output) throws IOException {
        output.writeInt(student.getGroupId());
        output.writeUTF(student.getName());
        output.writeUTF(student.getHometown());
        output.writeLong(student.getBirthDate().getTime());
        output.writeBoolean(student.isHasDormitory());
        output.writeDouble(student.getAverageScore());
    }

    @Override
    public Student deserializeFromFile(DataInput input) throws IOException {
        int groupId = input.readInt();
        String name = input.readUTF();
        String hometown = input.readUTF();
        Date birthDate = new Date(input.readLong());
        boolean hasDormitory = input.readBoolean();
        double averageScore = input.readDouble();
        return new Student(groupId, name, hometown, birthDate, hasDormitory, averageScore);
    }

    @Override
    public String getType() {
        return "Student";
    }
}