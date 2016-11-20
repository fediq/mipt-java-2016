package ru.mipt.java2016.homework.g595.tkachenko.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import javax.xml.bind.ValidationException;

import java.io.*;
import java.util.Date;

/**
 * Created by Dmitry on 20/11/2016.
 */

public class KlabertancOptimizedStorageTest extends KeyValueStoragePerformanceTest {

    public class StudentSerialization extends Serialization<Student> {
        @Override
        public Student read(DataInput input) throws IOException {
            return new Student(input.readInt(), input.readUTF(), input.readUTF(), new Date(input.readLong()),
                    input.readBoolean(), input.readDouble());
        }

        @Override
        public void write(DataOutput output, Student x) throws IOException {
            output.writeInt(x.getGroupId());
            output.writeUTF(x.getName());
            output.writeUTF(x.getHometown());
            output.writeLong(x.getBirthDate().getTime());
            output.writeBoolean(x.isHasDormitory());
            output.writeDouble(x.getAverageScore());
        }
    }

    public class StudentKeySerialization extends Serialization<StudentKey> {

        @Override
        public StudentKey read(DataInput input) throws IOException {
            return new StudentKey(input.readInt(), input.readUTF());
        }

        @Override
        public void write(DataOutput output, StudentKey x) throws IOException {
            output.writeInt(x.getGroupId());
            output.writeUTF(x.getName());
        }
    }

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        KlabertancOptimizedStorage<String, String> res = null;
        try {
            try {
                res = new KlabertancOptimizedStorage<>(path, new StringSerialization(),
                        new StringSerialization());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } catch (ValidationException ve) {
            System.out.println(ve.getMessage());
        }
        return res;
    }


    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        KlabertancOptimizedStorage<Integer, Double> res = null;
        try {
            try {
                res = new KlabertancOptimizedStorage<>(path, new IntSerialization(),
                        new DoubleSerialization());
            } catch (IOException excep) {
                System.out.println(excep.getMessage());
            }
        } catch (ValidationException ve) {
            System.out.println(ve.getMessage());
        }
        return res;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        KlabertancOptimizedStorage<StudentKey, Student> res = null;
        try {
            try {
                res = new KlabertancOptimizedStorage<>(path, new StudentKeySerialization(),
                        new StudentSerialization());
            } catch (IOException excep) {
                System.out.println(excep.getMessage());
            }
        } catch (ValidationException ve) {
            System.out.println(ve.getMessage());
        }
        return res;
    }
}
