package ru.mipt.java2016.homework.g595.gusarova.task3;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Дарья on 19.11.2016.
 */
public class SerializersAndDeserializers {
    public static class SerializerAndDeserializerForInteger implements SerializerAndDeserializer<Integer> {
        @Override
        public void serialize(Integer data, DataOutput file) throws IOException {
            file.writeInt(data);
        }

        @Override
        public Integer deserialize(DataInput file) throws IOException {
            return file.readInt();
        }
    }

    public static class SerializerAndDeserializerForLong implements SerializerAndDeserializer<Long> {
        @Override
        public void serialize(Long data, DataOutput file) throws IOException {
            file.writeLong(data);
        }

        @Override
        public Long deserialize(DataInput file) throws IOException {
            return file.readLong();
        }
    }

    public static class SerializerAndDeserializerForDouble implements SerializerAndDeserializer<Double> {
        @Override
        public void serialize(Double data, DataOutput file) throws IOException {
            file.writeDouble(data);
        }

        @Override
        public Double deserialize(DataInput file) throws IOException {
            return file.readDouble();
        }
    }

    public static class SerializerAndDeserializerForBoolean implements SerializerAndDeserializer<Boolean> {
        @Override
        public void serialize(Boolean data, DataOutput file) throws IOException {
            file.writeBoolean(data);
        }

        @Override
        public Boolean deserialize(DataInput file) throws IOException {
            return file.readBoolean();
        }
    }

    public static class SerializerAndDeserializerForString implements SerializerAndDeserializer<String> {
        @Override
        public void serialize(String data, DataOutput file) throws IOException {
            file.writeUTF(data);
        }

        @Override
        public String deserialize(DataInput file) throws IOException {
            return file.readUTF();
        }
    }

    public static class SerializerAndDeserializerForStudentKey implements SerializerAndDeserializer<StudentKey> {
        @Override
        public void serialize(StudentKey data, DataOutput file) throws IOException {
            file.writeInt(data.getGroupId());
            file.writeUTF(data.getName());
        }

        @Override
        public StudentKey deserialize(DataInput file) throws IOException {
            StudentKey temp = new StudentKey(file.readInt(), file.readUTF());
            return temp;
        }
    }

    public static class SerializerAndDeserializerForStudent implements SerializerAndDeserializer<Student> {
        @Override
        public void serialize(Student data, DataOutput file) throws IOException {
            file.writeInt(data.getGroupId());
            file.writeUTF(data.getName());
            file.writeUTF(data.getHometown());
            file.writeLong(data.getBirthDate().getTime());
            file.writeBoolean(data.isHasDormitory());
            file.writeDouble(data.getAverageScore());
        }

        @Override
        public Student deserialize(DataInput file) throws IOException {
            Student temp = new Student(file.readInt(), file.readUTF(), file.readUTF(),
                    new Date(file.readLong()), file.readBoolean(),
                    file.readDouble());
            return temp;
        }
    }
}
