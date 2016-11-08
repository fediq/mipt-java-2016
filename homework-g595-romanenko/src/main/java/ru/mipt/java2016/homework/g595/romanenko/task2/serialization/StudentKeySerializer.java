package ru.mipt.java2016.homework.g595.romanenko.task2.serialization;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task2.serialization
 *
 * @author Ilya I. Romanenko
 * @since 26.10.16
 **/
public class StudentKeySerializer implements SerializationStrategy<StudentKey> {

    private static final StudentKeySerializer STUDENT_KEY_SERIALIZER = new StudentKeySerializer();

    public static StudentKeySerializer getInstance() {
        return STUDENT_KEY_SERIALIZER;
    }


    @Override
    public void serializeToStream(StudentKey studentKey, OutputStream outputStream) throws IOException {
        IntegerSerializer.getInstance().serializeToStream(studentKey.getGroupId(), outputStream);
        StringSerializer.getInstance().serializeToStream(studentKey.getName(), outputStream);
    }

    @Override
    public int getBytesSize(StudentKey studentKey) {
        return IntegerSerializer.getInstance().getBytesSize(studentKey.getGroupId()) +
                StringSerializer.getInstance().getBytesSize(studentKey.getName());
    }

    @Override
    public StudentKey deserializeFromStream(InputStream inputStream) throws IOException {
        Integer groupId = IntegerSerializer.getInstance().deserializeFromStream(inputStream);
        String name = StringSerializer.getInstance().deserializeFromStream(inputStream);
        return new StudentKey(groupId, name);
    }
}
