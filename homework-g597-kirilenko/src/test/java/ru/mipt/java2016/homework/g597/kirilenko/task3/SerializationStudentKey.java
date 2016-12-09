package ru.mipt.java2016.homework.g597.kirilenko.task3;

import ru.mipt.java2016.homework.g597.kirilenko.task3.MySerialization.MySerialization;
import ru.mipt.java2016.homework.g597.kirilenko.task3.MySerialization.SerializationType;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;

public class SerializationStudentKey implements MySerialization<StudentKey> {
        private SerializationType.SerializationInteger serializeInt =
                SerializationType.SerializationInteger.getSerialization();
        private SerializationType.SerializationString serializeStr =
                SerializationType.SerializationString.getSerialization();
        private static SerializationStudentKey serialize = new SerializationStudentKey();

        private SerializationStudentKey() { }

        public static SerializationStudentKey getSerialization() {
            return serialize;
        }

        private boolean isCorrect(StudentKey value) {
            Integer group = value.getGroupId();
            String name = value.getName();
            if (group <= 0 || name == "" || name == " ") {
                return false;
            }
            return true;
        }

        @Override
        public void write(RandomAccessFile file, StudentKey value) throws IOException {
            serializeInt.write(file, value.getGroupId());
            serializeStr.write(file, value.getName());
        }

        @Override
        public StudentKey read(RandomAccessFile file) throws IOException {
            Integer group = serializeInt.read(file);
            String name = serializeStr.read(file);
            StudentKey value = new StudentKey(group, name);
            if (isCorrect(value)) {
                return value;
            } else {
                throw new IOException("Incorrect value");
            }
        }
    }