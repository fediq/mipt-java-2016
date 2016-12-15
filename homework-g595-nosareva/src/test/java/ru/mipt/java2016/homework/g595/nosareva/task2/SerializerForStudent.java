package ru.mipt.java2016.homework.g595.nosareva.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;


/**
 * Created by maria on 19.11.16.
 */
public class SerializerForStudent implements Serializer<Student> {

    @Override
    public void serializeToStream(Student value, DataOutput outStream) throws IOException {
        (new SerializerForInteger()).serializeToStream(value.getGroupId(), outStream);
        (new SerializerForString()).serializeToStream(value.getName(), outStream);
        (new SerializerForString()).serializeToStream(value.getHometown(), outStream);
        (new SerializerForDate()).serializeToStream(value.getBirthDate(), outStream);
        (new SerializerForBoolean()).serializeToStream(value.isHasDormitory(), outStream);
        (new SerializerForDouble()).serializeToStream(value.getAverageScore(), outStream);
    }

    @Override
    public Student deserializeFromStream(DataInput inputStream) throws IOException {
        return new Student(
                (new SerializerForInteger()).deserializeFromStream(inputStream),
                (new SerializerForString()).deserializeFromStream(inputStream),
                (new SerializerForString()).deserializeFromStream(inputStream),
                (new SerializerForDate()).deserializeFromStream(inputStream),
                (new SerializerForBoolean()).deserializeFromStream(inputStream),
                (new SerializerForDouble()).deserializeFromStream(inputStream));
    }
}
