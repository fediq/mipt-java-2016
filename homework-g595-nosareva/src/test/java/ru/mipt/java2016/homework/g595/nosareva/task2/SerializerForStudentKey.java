package ru.mipt.java2016.homework.g595.nosareva.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by maria on 19.11.16.
 */
public class SerializerForStudentKey implements Serializer<StudentKey> {

    @Override
    public void serializeToStream(StudentKey value, DataOutput outStream) throws IOException {
        (new SerializerForInteger()).serializeToStream(value.getGroupId(), outStream);
        (new SerializerForString()).serializeToStream(value.getName(), outStream);
    }

    @Override
    public StudentKey deserializeFromStream(DataInput inputStream) throws IOException {
        return new StudentKey(
                (new SerializerForInteger()).deserializeFromStream(inputStream),
                (new SerializerForString()).deserializeFromStream(inputStream));
    }
}
