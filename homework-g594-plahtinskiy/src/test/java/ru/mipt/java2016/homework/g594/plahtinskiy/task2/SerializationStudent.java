package ru.mipt.java2016.homework.g594.plahtinskiy.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Created by VadimPl on 31.10.16.
 */
public class SerializationStudent extends Serialization<Student> {

    @Override
    public void write(RandomAccessFile file, Student obj) throws IOException {
        SerializationInt serializationGroupId = new SerializationInt();
        SerializationString serializationName = new SerializationString();
        SerializationString serializationHometown = new SerializationString();
        SerializationDate serializationBirthDate = new SerializationDate();
        SerializationBoolean serializationHasDormitory = new SerializationBoolean();
        SerializationDouble serializationAverageScore = new SerializationDouble();

        serializationGroupId.write(file, obj.getGroupId());
        serializationName.write(file, obj.getName());
        serializationHometown.write(file, obj.getHometown());
        serializationBirthDate.write(file, obj.getBirthDate());
        serializationHasDormitory.write(file, obj.isHasDormitory());
        serializationAverageScore.write(file, obj.getAverageScore());
    }

    @Override
    public Student read(RandomAccessFile file) throws IOException {
        SerializationInt serializationGroupId = new SerializationInt();
        SerializationString serializationName = new SerializationString();
        SerializationString serializationHometown = new SerializationString();
        SerializationDate serializationBirthDate = new SerializationDate();
        SerializationBoolean serializationHasDormitory = new SerializationBoolean();
        SerializationDouble serializationAverageScore = new SerializationDouble();

        Integer groupId = serializationGroupId.read(file);
        String name = serializationName.read(file);
        String hometown = serializationHometown.read(file);
        Date birthDate = serializationBirthDate.read(file);
        Boolean hasDormitory = serializationHasDormitory.read(file);
        Double averegeScore = serializationAverageScore.read(file);
        return new Student(groupId, name, hometown, birthDate, hasDormitory, averegeScore);
    }


}
