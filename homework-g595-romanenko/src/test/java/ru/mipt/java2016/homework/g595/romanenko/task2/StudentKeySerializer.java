package ru.mipt.java2016.homework.g595.romanenko.task2;

import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.IntegerSerializer;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.SerializationStrategy;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.StringSerializer;
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
        Integer totalSize = getBytesSize(studentKey);
        IntegerSerializer.getInstance().serializeToStream(totalSize, outputStream);

        IntegerSerializer.getInstance().serializeToStream(studentKey.getGroupId(), outputStream);
        StringSerializer.getInstance().serializeToStream(studentKey.getName(), outputStream);
    }

    @Override
    public int getBytesSize(StudentKey studentKey) {
        return IntegerSerializer.getInstance().getBytesSize(0) +
                IntegerSerializer.getInstance().getBytesSize(studentKey.getGroupId()) +
                StringSerializer.getInstance().getBytesSize(studentKey.getName());
    }

    @Override
    public StudentKey deserializeFromStream(InputStream inputStream) throws IOException {
        Integer totalSize = IntegerSerializer.getInstance().deserializeFromStream(inputStream);

        Integer groupId = IntegerSerializer.getInstance().deserializeFromStream(inputStream);
        String name = StringSerializer.getInstance().deserializeFromStream(inputStream);
        return new StudentKey(groupId, name);
    }
}
