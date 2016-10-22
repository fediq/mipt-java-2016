package ru.mipt.java2016.homework.g595.romanenko.task2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Стратегия сериализации
 *
 * @author Ilya I. Romanenko
 * @since 21.10.16
 **/
class SerializersFactory {

    static class IntegerSerializer implements SerializationStrategy<Integer> {

        private static final IntegerSerializer serializer = new IntegerSerializer();
        private static final byte[] binaryRepresentation = new byte[Integer.BYTES];

        static IntegerSerializer getInstance() {
            return serializer;
        }

        @Override
        public byte[] serializeToBytes(Integer integer) {
            byte[] result = new byte[Integer.BYTES];
            for (int i = 0; i < Integer.BYTES; i++) {
                result[i] = (byte)(integer & 256);
                integer >>= 8;
            }
            return result;
        }

        @Override
        public void serializeToStream(Integer integer, OutputStream outputStream) throws IOException {
            outputStream.write(serializeToBytes(integer));
        }

        @Override
        public Integer deserializeFromStream(InputStream inputStream) throws IOException {
            inputStream.read(binaryRepresentation);
            return deserialize(binaryRepresentation);
        }

        @Override
        public Integer deserialize(byte[] bytes, int offset) {
            int result = 0;
            for (int i = 0; i < Integer.BYTES; i++) {
                result = (result << 8) + bytes[i + offset];
            }
            return result;
        }
    }

    static class DoubleSerializer implements SerializationStrategy<Double> {

        private final static DoubleSerializer serializer = new DoubleSerializer();
        private static long longRepresentation = 0;
        private final static byte[] binaryRepresentation = new byte[Double.BYTES];

        public static DoubleSerializer getInstance() {
            return serializer;
        }

        @Override
        public byte[] serializeToBytes(Double aDouble) {
            longRepresentation = Double.doubleToLongBits(aDouble);
            byte[] result = new byte[Double.BYTES];
            for (int i = 0; i < Double.BYTES; i++) {
                result[i] = (byte)(longRepresentation % 256);
                longRepresentation >>= 8;
            }
            return result;
        }

        @Override
        public void serializeToStream(Double aDouble, OutputStream outputStream) throws IOException {
            outputStream.write(serializeToBytes(aDouble));
        }

        @Override
        public Double deserializeFromStream(InputStream inputStream) throws IOException {
            inputStream.read(binaryRepresentation);
            return deserialize(binaryRepresentation);
        }

        @Override
        public Double deserialize(byte[] bytes, int offset) {
            longRepresentation = 0;
            for (int i = 0; i < Double.BYTES; i++)
                longRepresentation = (longRepresentation << 8) + bytes[offset + i];
            return Double.longBitsToDouble(longRepresentation);
        }
    }
}
