package ru.mipt.java2016.homework.g596.litvinov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by
 *
 * @author Stanislav A. Litvinov
 * @since 31.10.16.
 */
public class MySerializationKeyStudents implements MySerialization<StudentKey> {

    private static final MySerializationKeyStudents serialization =
            new MySerializationKeyStudents();
    private MySerializationInteger intSerializator = MySerializationInteger.getSerialization();
    private MySerializationString strSerialiazator = MySerializationString.getSerialization();

    public MySerializationKeyStudents getSerialization() {
        return serialization;
    }

    @Override
    public StudentKey read(DataInputStream file) throws IOException {
        int gid = intSerializator.read(file);
        String name = strSerialiazator.read(file);
        return new StudentKey(gid, name);
    }

    @Override
    public void write(DataOutputStream file, StudentKey object) throws IOException {
        intSerializator.write(file, object.getGroupId());
        strSerialiazator.write(file, object.getName());
    }
}
