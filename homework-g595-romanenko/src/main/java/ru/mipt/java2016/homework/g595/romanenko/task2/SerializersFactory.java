package ru.mipt.java2016.homework.g595.romanenko.task2;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;


/**
 * Стратегия сериализации
 *
 * @author Ilya I. Romanenko
 * @since 21.10.16
 **/
class SerializersFactory {

    private static int getAbsoluteValueOfByte(byte value) {
        return (value + 256) % 256;
    }

    private static abstract class SimpleIntegralTypeSerializer<IntegralType> implements SerializationStrategy<IntegralType> {

        int BYTES = 0;

        protected Long getLongRepresentation(IntegralType value) {
            return 0L;
        }

        protected IntegralType convertFromLong(Long value) {
            return null;
        }

        @Override
        public void serializeToStream(IntegralType value, OutputStream outputStream) throws IOException {
            byte[] binaryRepresentation = new byte[BYTES];
            Long longValue = getLongRepresentation(value);
            for (int i = 0; i < BYTES; i++) {
                binaryRepresentation[BYTES - i - 1] = longValue.byteValue();
                longValue >>= 8;
            }
            outputStream.write(binaryRepresentation);
        }

        @Override
        public IntegralType deserializeFromStream(InputStream inputStream) throws IOException {
            byte[] bytes = new byte[BYTES];
            inputStream.read(bytes);
            long value = 0;
            for (int i = 0; i < BYTES; i++) {
                value = (value << 8) + getAbsoluteValueOfByte(bytes[i]);
            }
            return convertFromLong(value);
        }
    }

    static class IntegerSerializer extends SimpleIntegralTypeSerializer<Integer> {

        private static final IntegerSerializer serializer = new IntegerSerializer();

        static IntegerSerializer getInstance() {
            return serializer;
        }

        IntegerSerializer() {
            super.BYTES = Integer.BYTES;
        }

        protected Long getLongRepresentation(Integer value) {
            return value.longValue();
        }

        protected Integer convertFromLong(Long value) {
            return value.intValue();
        }

    }

    static class LongSerializer extends SimpleIntegralTypeSerializer<Long> {

        private static final LongSerializer serializer = new LongSerializer();

        static LongSerializer getInstance() {
            return serializer;
        }

        LongSerializer() {
            super.BYTES = Long.BYTES;
        }

        protected Long getLongRepresentation(Long value) {
            return value;
        }

        protected Long convertFromLong(Long value) {
            return value;
        }
    }

    static class DoubleSerializer extends SimpleIntegralTypeSerializer<Double> {

        private final static DoubleSerializer serializer = new DoubleSerializer();

        public static DoubleSerializer getInstance() {
            return serializer;
        }

        DoubleSerializer() {
            super.BYTES = Double.BYTES;
        }

        protected Long getLongRepresentation(Double value) {
            return Double.doubleToLongBits(value);
        }

        protected Double convertFromLong(Long value) {
            return Double.longBitsToDouble(value);
        }
    }

    static class DateSerializer extends SimpleIntegralTypeSerializer<Date> {

        static final DateSerializer serializer = new DateSerializer();

        public static DateSerializer getInstance() {
            return serializer;
        }

        DateSerializer() {
            super.BYTES = Long.BYTES;
        }

        protected Long getLongRepresentation(Date value) {
            return value.getTime();
        }

        protected Date convertFromLong(Long value) {
            return new Date(value);
        }
    }

    static class StringSerializer implements SerializationStrategy<String> {

        private final static StringSerializer serializer = new StringSerializer();

        public static StringSerializer getInstance() {
            return serializer;
        }

        @Override
        public void serializeToStream(String s, OutputStream outputStream) throws IOException {
            IntegerSerializer.getInstance().serializeToStream(s.length(), outputStream);
            outputStream.write(s.getBytes());
        }

        @Override
        public String deserializeFromStream(InputStream inputStream) throws IOException {
            Integer length = IntegerSerializer.getInstance().deserializeFromStream(inputStream);
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            return new String(bytes);
        }
    }

    static class StudentKeySerializer implements SerializationStrategy<StudentKey> {

        private final static StudentKeySerializer serializer = new StudentKeySerializer();

        public static StudentKeySerializer getInstance() {
            return serializer;
        }


        @Override
        public void serializeToStream(StudentKey studentKey, OutputStream outputStream) throws IOException {
            IntegerSerializer.getInstance().serializeToStream(studentKey.getGroupId(), outputStream);
            StringSerializer.getInstance().serializeToStream(studentKey.getName(), outputStream);
        }

        @Override
        public StudentKey deserializeFromStream(InputStream inputStream) throws IOException {
            Integer groupId = IntegerSerializer.getInstance().deserializeFromStream(inputStream);
            String name = StringSerializer.getInstance().deserializeFromStream(inputStream);
            return new StudentKey(groupId, name);
        }
    }

    static class StudentSerializer implements SerializationStrategy<Student> {

        private final static StudentSerializer serializer = new StudentSerializer();

        public static StudentSerializer getInstance() {
            return serializer;
        }

        @Override
        public void serializeToStream(Student student, OutputStream outputStream) throws IOException {
            StudentKey studentKey = new StudentKey(student.getGroupId(), student.getName());
            StudentKeySerializer.getInstance().serializeToStream(studentKey, outputStream);

            StringSerializer.getInstance().serializeToStream(student.getHometown(), outputStream);

            DateSerializer.getInstance().serializeToStream(student.getBirthDate(), outputStream);

            byte isHasDormitory = (student.isHasDormitory() ? (byte) 1 : 0);
            outputStream.write(isHasDormitory);

            DoubleSerializer.getInstance().serializeToStream(student.getAverageScore(), outputStream);
        }

        @Override
        public Student deserializeFromStream(InputStream inputStream) throws IOException {
            StudentKey studentKey = StudentKeySerializer.getInstance().deserializeFromStream(inputStream);

            String hometown = StringSerializer.getInstance().deserializeFromStream(inputStream);

            Date birthDate = DateSerializer.getInstance().deserializeFromStream(inputStream);

            boolean isHasDormitory = (inputStream.read() != 0);

            Double averageScore = DoubleSerializer.getInstance().deserializeFromStream(inputStream);

            return new Student(studentKey.getGroupId(), studentKey.getName(), hometown, birthDate, isHasDormitory, averageScore);
        }
    }
}
