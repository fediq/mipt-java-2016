package ru.mipt.java2016.homework.g595.murzin.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by Дмитрий Мурзин on 24.10.16.
 */
public interface SerializationStrategy<Value> {

    /**
     * Записать сериализованное значение в поток
     */
    void serializeToStream(Value value, DataOutputStream output) throws IOException;

    /**
     * Прочесть сериализованное значение из текущего места в потоке
     */
    Value deserializeFromStream(DataInputStream input) throws IOException;

    SerializationStrategy<String> FOR_STRING = new SerializationStrategy<String>() {
        @Override
        public void serializeToStream(String s, DataOutputStream output) throws IOException {
            output.writeInt(s.length());
            output.write(s.getBytes(StandardCharsets.UTF_8));
//            this method doesn't work with strings longer than 65535
//            output.writeUTF(s);
        }

        @Override
        public String deserializeFromStream(DataInputStream input) throws IOException {
            int length = input.readInt();
            byte[] bytes = new byte[length];
            int actualLength = input.read(bytes);
            if (actualLength < length) {
                throw new EOFException("Can't read UTF string: required " + length +
                        " bytes, but only " + actualLength + " available");
            }
            return new String(bytes, StandardCharsets.UTF_8);
        }
    };

    SerializationStrategy<Integer> FOR_INTEGER = new SerializationStrategy<Integer>() {
        @Override
        public void serializeToStream(Integer integer, DataOutputStream output) throws IOException {
            output.writeInt(integer);
        }

        @Override
        public Integer deserializeFromStream(DataInputStream input) throws IOException {
            return input.readInt();
        }
    };

    SerializationStrategy<Double> FOR_DOUBLE = new SerializationStrategy<Double>() {
        @Override
        public void serializeToStream(Double value, DataOutputStream output) throws IOException {
            output.writeDouble(value);
        }

        @Override
        public Double deserializeFromStream(DataInputStream input) throws IOException {
            return input.readDouble();
        }
    };
}
