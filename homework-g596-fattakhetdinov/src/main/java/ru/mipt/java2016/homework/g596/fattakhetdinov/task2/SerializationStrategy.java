package ru.mipt.java2016.homework.g596.fattakhetdinov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;


public interface SerializationStrategy<T> {
    void serializeToFile(T value, DataOutputStream output) throws IOException;

    T deserializeFromFile(DataInputStream input) throws IOException;

    String getType(); //Возвращает тип стратегии сериализации

    SerializationStrategy<String> STRING_SERIALIZATOR = new SerializationStrategy<String>() {
        @Override
        public void serializeToFile(String str, DataOutputStream output) throws IOException {
            output.writeUTF(str);
        }

        @Override
        public String deserializeFromFile(DataInputStream input) throws IOException {
            return input.readUTF();
        }

        @Override
        public String getType() {
            return "String";
        }
    };

    SerializationStrategy<Integer> INTEGER_SERIALIZATOR = new SerializationStrategy<Integer>() {
        @Override
        public void serializeToFile(Integer value, DataOutputStream output) throws IOException {
            output.writeInt(value);
        }

        @Override
        public Integer deserializeFromFile(DataInputStream input) throws IOException {
            return input.readInt();
        }

        @Override
        public String getType() {
            return "Integer";
        }
    };

    SerializationStrategy<Double> DOUBLE_SERIALIZATOR = new SerializationStrategy<Double>() {
        @Override
        public void serializeToFile(Double value, DataOutputStream output) throws IOException {
            output.writeDouble(value);
        }

        @Override
        public Double deserializeFromFile(DataInputStream input) throws IOException {
            return input.readDouble();
        }

        @Override
        public String getType() {
            return "Double";
        }
    };

    SerializationStrategy<StudentKey> STUDENT_KEY_SERIALIZATOR = new SerializationStrategy<StudentKey>() {
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

    SerializationStrategy<Student> STUDENT_SERIALIZATOR = new SerializationStrategy<Student>() {
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

}
