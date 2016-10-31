package ru.mipt.java2016.homework.g596.fattakhetdinov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

public class MyFileStorageTest extends AbstractSingleFileStorageTest {

    private static SerializationStrategy<StudentKey> STUDENT_KEY_SERIALIZATOR
            = new SerializationStrategy<StudentKey>() {
        @Override
        public void serializeToFile(StudentKey studentKey, DataOutputStream output) throws IOException {
            output.writeInt(studentKey.getGroupId());
            output.writeUTF(studentKey.getName());
        }

        @Override
        public StudentKey deserializeFromFile(DataInputStream input) throws IOException {
            int groupId = input.readInt();
            String name = input.readUTF();
            return new StudentKey(groupId, name);
        }

        @Override
        public String getType() {
            return "StudentKey";
        }
    };

    private static SerializationStrategy<Student> STUDENT_SERIALIZATOR = new SerializationStrategy<Student>() {
        @Override
        public void serializeToFile(Student student, DataOutputStream output) throws IOException {
            output.writeInt(student.getGroupId());
            output.writeUTF(student.getName());
            output.writeUTF(student.getHometown());
            output.writeLong(student.getBirthDate().getTime());
            output.writeBoolean(student.isHasDormitory());
            output.writeDouble(student.getAverageScore());
        }

        @Override
        public Student deserializeFromFile(DataInputStream input) throws IOException {
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
    };

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        KeyValueStorage<String, String> result = null;
        try {
            result = new MyKeyValueStorage<>(path, SerializationStrategy.STRING_SERIALIZATOR,
                    SerializationStrategy.STRING_SERIALIZATOR);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        KeyValueStorage<Integer, Double> result = null;
        try {
            result = new MyKeyValueStorage<>(path, SerializationStrategy.INTEGER_SERIALIZATOR,
                    SerializationStrategy.DOUBLE_SERIALIZATOR);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        KeyValueStorage<StudentKey, Student> result = null;
        try {
            result = new MyKeyValueStorage<>(path, STUDENT_KEY_SERIALIZATOR,
                    STUDENT_SERIALIZATOR);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
