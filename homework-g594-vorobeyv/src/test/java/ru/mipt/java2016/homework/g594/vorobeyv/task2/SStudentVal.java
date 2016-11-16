package ru.mipt.java2016.homework.g594.vorobeyv.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Morell on 30.10.2016.
 */
public class SStudentVal extends Serializator<Student> {
    @Override
    public Student read(DataInputStream input) throws IOException {
        // Сделал отдельно считывание в groupId, name , а не сразу в конструктор , чтобы
        // было удобнее проверять правильность считывания
        int groupId = input.readInt();
        String name = input.readUTF();
        String hometown = input.readUTF();
        long date = input.readLong();
        Date birthDate = new Date(date);
        boolean hasDormitory = input.readBoolean();
        double averageScore = input.readDouble();
        return new Student(groupId, name, hometown,
                birthDate, hasDormitory, averageScore);
    }

    @Override
    public void write(DataOutputStream output, Student value) throws IOException {
        output.writeInt(value.getGroupId());
        output.writeUTF(value.getName());
        output.writeUTF(value.getHometown());
        output.writeLong(value.getBirthDate().getTime());
        output.writeBoolean(value.isHasDormitory());
        output.writeDouble(value.getAverageScore());
    }
}
