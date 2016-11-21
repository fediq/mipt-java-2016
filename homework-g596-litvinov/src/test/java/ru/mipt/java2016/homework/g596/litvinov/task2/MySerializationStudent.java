package ru.mipt.java2016.homework.g596.litvinov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import ru.mipt.java2016.homework.tests.task2.Student;

/**
 * Created by
 *
 * @author Stanislav A. Litvinov
 * @since 31.10.16.
 */
public class MySerializationStudent implements MySerialization<Student> {
    public static MySerializationStudent serialization = new MySerializationStudent();
    private final MySerializationDate dateSerializator = MySerializationDate.getSerialization();
    private final MySerializationInteger intSerializator =
            MySerializationInteger.getSerialization();
    private final MySerializationString strSerializator = MySerializationString.getSerialization();
    private final MySerializationDouble dblSerializator = MySerializationDouble.getSerialization();
    private final MySerializationBool boolSerializator = MySerializationBool.getSerialization();

    @Override
    public Student read(DataInputStream file) throws IOException {
        int gid = intSerializator.read(file);
        String name = strSerializator.read(file);
        Date birthDate = dateSerializator.read(file);
        String hometwn = strSerializator.read(file);
        boolean dorm = boolSerializator.read(file);
        double avgScore = dblSerializator.read(file);
        return new Student(gid, name, hometwn, birthDate, dorm, avgScore);
    }

    @Override
    public void write(DataOutputStream file, Student student) throws IOException {
        intSerializator.write(file, student.getGroupId());
        strSerializator.write(file, student.getName());
        dateSerializator.write(file, student.getBirthDate());
        strSerializator.write(file, student.getHometown());
        boolSerializator.write(file, student.isHasDormitory());
        dblSerializator.write(file, student.getAverageScore());
    }

    MySerializationStudent getSerialization() {
        return serialization;
    }

}
