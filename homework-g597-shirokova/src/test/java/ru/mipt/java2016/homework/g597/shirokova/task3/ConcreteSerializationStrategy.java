package ru.mipt.java2016.homework.g597.shirokova.task3;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

class ConcreteSerializationStrategy {

    static class BooleanConcreteStrategy implements SerializationStrategy<Boolean> {
        @Override
        public void serialize(DataOutput output, Boolean value) throws IOException {
            output.writeBoolean(value);
        }

        @Override
        public Boolean deserialize(DataInput input) throws IOException {
            return input.readBoolean();
        }
    }

    static class DateConcreteStrategy implements SerializationStrategy<Date> {
        @Override
        public void serialize(DataOutput output, Date value) throws IOException {
            output.writeLong(value.getTime());
        }

        @Override
        public Date deserialize(DataInput input) throws IOException {
            return new Date(input.readLong());
        }
    }

    static class DoubleConcreteStrategy implements SerializationStrategy<Double> {
        @Override
        public void serialize(DataOutput output, Double value) throws IOException {
            output.writeDouble(value);
        }

        @Override
        public Double deserialize(DataInput input) throws IOException {
            return input.readDouble();
        }
    }

    static class IntegerConcreteStrategy implements SerializationStrategy<Integer> {
        @Override
        public void serialize(DataOutput output, Integer value) throws IOException {
            output.writeInt(value);
        }

        @Override
        public Integer deserialize(DataInput input) throws IOException {
            return input.readInt();
        }
    }

    static class LongConcreteStrategy implements SerializationStrategy<Long> {
        @Override
        public void serialize(DataOutput output, Long value) throws IOException {
            output.writeLong(value);
        }

        @Override
        public Long deserialize(DataInput input) throws IOException {
            return input.readLong();
        }
    }

    static class StringConcreteStrategy implements SerializationStrategy<String> {
        @Override
        public void serialize(DataOutput output, String value) throws IOException {
            output.writeUTF(value);
        }

        @Override
        public String deserialize(DataInput input) throws IOException {
            return input.readUTF();
        }
    }

    static class StudentKeyConcreteStrategy implements SerializationStrategy<StudentKey> {
        @Override
        public void serialize(DataOutput output, StudentKey value) throws IOException {
            (new IntegerConcreteStrategy()).serialize(output, value.getGroupId());
            (new StringConcreteStrategy()).serialize(output, value.getName());
        }

        @Override
        public StudentKey deserialize(DataInput input) throws IOException {
            return new StudentKey(
                    (new IntegerConcreteStrategy()).deserialize(input),
                    (new StringConcreteStrategy()).deserialize(input));
        }
    }

    static class StudentConcreteStrategy implements SerializationStrategy<Student> {
        @Override
        public void serialize(DataOutput output, Student value) throws IOException {
            (new IntegerConcreteStrategy()).serialize(output, value.getGroupId());
            (new StringConcreteStrategy()).serialize(output, value.getName());
            (new StringConcreteStrategy()).serialize(output, value.getHometown());
            (new DateConcreteStrategy()).serialize(output, value.getBirthDate());
            (new BooleanConcreteStrategy()).serialize(output, value.isHasDormitory());
            (new DoubleConcreteStrategy()).serialize(output, value.getAverageScore());
        }

        @Override
        public Student deserialize(DataInput input) throws IOException {
            return new Student(
                    (new IntegerConcreteStrategy()).deserialize(input),
                    (new StringConcreteStrategy()).deserialize(input),
                    (new StringConcreteStrategy()).deserialize(input),
                    (new DateConcreteStrategy()).deserialize(input),
                    (new BooleanConcreteStrategy()).deserialize(input),
                    (new DoubleConcreteStrategy()).deserialize(input));
        }
    }
}