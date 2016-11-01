package ru.mipt.java2016.homework.g597.kirilenko.task2.MySerialization;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Created by Natak on 30.10.2016.
 */
public class SerializationType {

    public static class SerializationBoolean implements MySerialization<Boolean> {
        private static SerializationBoolean serialize = new SerializationBoolean();

        private SerializationBoolean() { }

        public static SerializationBoolean getSerialization() {
            return serialize;
        }

        @Override
        public void write(RandomAccessFile file, Boolean value) throws IOException {
            try {
                file.writeBoolean(value);
            } catch (IOException e) {
                throw new IOException("File write error");
            }
        }

        @Override
        public Boolean read(RandomAccessFile file) throws IOException {
            Boolean value = null;
            try {
                value = file.readBoolean();
            } catch (IOException e) {
                throw new IOException("File read error");
            }
            return value;
        }
    }

    public static class SerializationDate implements MySerialization<Date> {
        private static SerializationDate serialize = new SerializationDate();

        private SerializationDate() { }

        public static SerializationDate getSerialization() {
            return serialize;
        }

        @Override
        public void write(RandomAccessFile file, Date value) throws IOException {
            try {
                long time = value.getTime();
                file.writeLong(time);
            } catch (IOException e) {
                throw new IOException("File write error");
            }
        }

        @Override
        public Date read(RandomAccessFile file) throws IOException {
            Date value = null;
            try {
                Long time = file.readLong();
                value = new Date(time);
                return value;
            } catch (IOException e) {
                throw new IOException("File read error");
            }
        }
    }

    public static class SerializationDouble implements MySerialization<Double> {
        private static SerializationDouble serialize = new SerializationDouble();

        private SerializationDouble() { }

        public static SerializationDouble getSerialization() {
            return serialize;
        }

        @Override
        public void write(RandomAccessFile file, Double value) throws IOException {
            try {
                file.writeDouble(value);
            } catch (IOException e) {
                throw new IOException("File write error");
            }
        }

        @Override
        public Double read(RandomAccessFile file) throws IOException {
            Double value = null;
            try {
                value = file.readDouble();
            } catch (IOException e) {
                throw new IOException("File read error");
            }
            return value;
        }
    }

    public static class SerializationInteger implements MySerialization<Integer> {
        private static SerializationInteger serialize = new SerializationInteger();

        private SerializationInteger() { }

        public static SerializationInteger getSerialization() {
            return serialize;
        }

        @Override
        public void write(RandomAccessFile file, Integer value) throws IOException {
            try {
                file.writeInt(value);
            } catch (IOException e) {
                throw new IOException("File write error");
            }
        }

        @Override
        public Integer read(RandomAccessFile file) throws IOException {
            Integer value = null;
            try {
                value = file.readInt();
            } catch (IOException e) {
                throw new IOException("File read error");
            }
            return value;
        }
    }

    public static class SerializationString implements MySerialization<String> {
        private static SerializationString serialize = new SerializationString();

        private SerializationString() { }

        public static SerializationString getSerialization() {
            return serialize;
        }

        @Override
        public void write(RandomAccessFile file, String value) throws IOException {
            byte[] temp = value.getBytes();
            try {
                file.writeInt(temp.length);
                file.write(temp);
            } catch (IOException e) {
                throw new IOException("File write error");
            }
        }

        @Override
        public String read(RandomAccessFile file) throws IOException {
            try {
                int size = file.readInt();
                byte[] temp = new byte[size];
                file.readFully(temp);
                return new String(temp);
            } catch (IOException e) {
                throw new IOException("File read error");
            }
        }
    }

//    public static class SerializationStudent implements MySerialization<Student> {
//        private SerializationType.SerializationInteger serializeInt =
//                SerializationType.SerializationInteger.getSerialization();
//        private SerializationType.SerializationString serializeStr =
//                SerializationType.SerializationString.getSerialization();
//        private SerializationType.SerializationDate serializeDate =
//                SerializationType.SerializationDate.getSerialization();
//        private SerializationType.SerializationBoolean serializeBool =
//                SerializationType.SerializationBoolean.getSerialization();
//        private SerializationType.SerializationDouble serializeDouble =
//                SerializationType.SerializationDouble.getSerialization();
//        private static SerializationStudent serialize = new SerializationStudent();
//
//        private SerializationStudent() { }
//
//        public static SerializationStudent getSerialization() {
//            return serialize;
//        }
//
//        private boolean isCorrect(Student value) {
//            Integer group = value.getGroupId();
//            String name = value.getName();
//            Date birth = value.getBirthDate();
//            int year = birth.getYear();
//            Double score = value.getAverageScore();
//            if (group <= 0 || name == "" || name == " " || score < 0 || year < -1900 || year > 116) {
//                return false;
//            }
//            return true;
//        }
//
//        @Override
//        public void write(RandomAccessFile file, Student value) throws IOException {
//            serializeInt.write(file, value.getGroupId());
//            serializeStr.write(file, value.getName());
//            serializeStr.write(file, value.getHometown());
//            serializeDate.write(file, value.getBirthDate());
//            serializeBool.write(file, value.isHasDormitory());
//            serializeDouble.write(file, value.getAverageScore());
//        }
//
//        @Override
//        public Student read(RandomAccessFile file) throws IOException {
//            Integer group = serializeInt.read(file);
//            String name = serializeStr.read(file);
//            String home = serializeStr.read(file);
//            Date birth = serializeDate.read(file);
//            Boolean dormitory = serializeBool.read(file);
//            Double score = serializeDouble.read(file);
//            Student value = new Student(group, name, home, birth, dormitory, score);
//            if (isCorrect(value)) {
//                return value;
//            } else {
//                throw new IOException("Incorrect value");
//            }
//        }
//    }
//
//    public static class SerializationStudentKey implements MySerialization<StudentKey> {
//        private SerializationType.SerializationInteger serializeInt =
//                SerializationType.SerializationInteger.getSerialization();
//        private SerializationType.SerializationString serializeStr =
//                SerializationType.SerializationString.getSerialization();
//        private static SerializationStudentKey serialize = new SerializationStudentKey();
//
//        private SerializationStudentKey() { }
//
//        public static SerializationStudentKey getSerialization() {
//            return serialize;
//        }
//
//        private boolean isCorrect(StudentKey value) {
//            Integer group = value.getGroupId();
//            String name = value.getName();
//            if (group <= 0 || name == "" || name == " ") {
//                return false;
//            }
//            return true;
//        }
//
//        @Override
//        public void write(RandomAccessFile file, StudentKey value) throws IOException {
//            serializeInt.write(file, value.getGroupId());
//            serializeStr.write(file, value.getName());
//        }
//
//        @Override
//        public StudentKey read(RandomAccessFile file) throws IOException {
//            Integer group = serializeInt.read(file);
//            String name = serializeStr.read(file);
//            StudentKey value = new StudentKey(group, name);
//            if (isCorrect(value)) {
//                return value;
//            } else {
//                throw new IOException("Incorrect value");
//            }
//        }
//    }
}
