package ru.mipt.java2016.homework.g597.kasimova.task2;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Надежда on 29.10.2016.
 */

public interface MSerialization<Type> {
    void serializeToStream(Type value, DataOutputStream outStream);

    Type deserializeFromStream(DataInputStream inStream);

    static final MSerialization<String> STRING_SERIALIZER = new MSerialization<String>() {
        @Override
        public void serializeToStream(String value, DataOutputStream outStream) {
            try {
                outStream.writeUTF(value);
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
        }

        @Override
        public String deserializeFromStream(DataInputStream inStream) {
            try {
                return inStream.readUTF();
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
            return null;
        }
    };

    static final MSerialization<Integer> INTEGER_SERIALIZER = new MSerialization<Integer>() {
        @Override
        public void serializeToStream(Integer value, DataOutputStream outStream) {
            try {
                outStream.writeInt(value);
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
        }

        @Override
        public Integer deserializeFromStream(DataInputStream inStream) {
            try {
                return inStream.readInt();
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
            return null;
        }
    };

    static final MSerialization<Double> DOUBLE_SERIALIZER = new MSerialization<Double>() {
        @Override
        public void serializeToStream(Double value, DataOutputStream outStream) {
            try {
                outStream.writeDouble(value);
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
        }

        @Override
        public Double deserializeFromStream(DataInputStream inStream) {
            try {
                return inStream.readDouble();
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
            return null;
        }
    };

    static final MSerialization<Student> STUDENT_SERIALIZER = new MSerialization<Student>() {
        @Override
        public void serializeToStream(Student value, DataOutputStream outStream) {
            try {
                outStream.writeInt(value.getGroupId());
                outStream.writeUTF(value.getName());
                outStream.writeUTF(value.getHometown());
                outStream.writeLong(value.getBirthDate().getTime());
                outStream.writeBoolean(value.isHasDormitory());
                outStream.writeDouble(value.getAverageScore());
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
        }

        @Override
        public Student deserializeFromStream(DataInputStream inStream) {
            try {
                return new Student(
                        inStream.readInt(),
                        inStream.readUTF(),
                        inStream.readUTF(),
                        new Date(inStream.readLong()),
                        inStream.readBoolean(),
                        inStream.readDouble()
                );
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
            return null;
        }
    };

    static final MSerialization<StudentKey> STUDENT_KEY_SERIALIZER = new MSerialization<StudentKey>() {
        @Override
        public void serializeToStream(StudentKey value, DataOutputStream outStream) {
            try {
                outStream.writeInt(value.getGroupId());
                outStream.writeUTF(value.getName());
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
        }

        @Override
        public StudentKey deserializeFromStream(DataInputStream inStream) {
            try {
                return new StudentKey(inStream.readInt(), inStream.readUTF());
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
            return null;
        }
    };
}