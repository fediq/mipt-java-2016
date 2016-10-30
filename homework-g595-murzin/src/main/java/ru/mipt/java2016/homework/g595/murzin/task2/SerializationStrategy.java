package ru.mipt.java2016.homework.g595.murzin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
            output.writeUTF(s);
        }

        @Override
        public String deserializeFromStream(DataInputStream input) throws IOException {
            return input.readUTF();
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
