package tests.task2;

import ru.mipt.java2016.homework.g595.efimochkin.task3.*;
import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;


/**
 * Created by sergejefimockin on 28.11.16.
 */
public class StudentSerialization implements BaseSerialization<Student> {

    private BooleanSerialization booleanSerialization = BooleanSerialization.getInstance();
    private DateSerialization dateSerialization = DateSerialization.getInstance();
    private DoubleSerialization doubleSerialization = DoubleSerialization.getInstance();
    private IntegerSerialization integerSerialization = IntegerSerialization.getInstance();
    //private LongSerialization longSerialization = LongSerialization.getInstance();
    private StringSerialization stringSerialization = StringSerialization.getInstance();

    private static StudentSerialization instance = new StudentSerialization();

    public static StudentSerialization getInstance() {return instance;}

    private StudentSerialization() { }

    @Override
    public Long write(RandomAccessFile file, Student object) throws IOException {
        try {
            Long offset = file.getFilePointer();
            integerSerialization.write(file, object.getGroupId());
            stringSerialization.write(file, object.getName());
            stringSerialization.write(file, object.getHometown());
            dateSerialization.write(file, object.getBirthDate());
            booleanSerialization.write(file, object.isHasDormitory());
            doubleSerialization.write(file, object.getAverageScore());
            return offset;
        } catch (IOException e) {
            throw new IOException("Could not write to file.");
        }
    }

    @Override
    public Student read(RandomAccessFile file) throws IOException {
        try {
            int groupId = integerSerialization.read(file);
            String name = stringSerialization.read(file);
            String hometown = stringSerialization.read(file);
            Date birthDate = dateSerialization.read(file);
            Boolean hasDormitory = booleanSerialization.read(file);
            Double averageScore = doubleSerialization.read(file);
            return new Student(groupId, name, hometown, birthDate, hasDormitory, averageScore);
        } catch (IOException e) {
            throw new IOException("Could not read from file.");
        }
    }
}

