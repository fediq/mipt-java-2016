package ru.mipt.java2016.homework.g596.fattakhetdinov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface SerializationStrategy<T> {
    void serializeToFile(T value, DataOutputStream output) throws IOException;

    T deserializeFromFile(DataInputStream input) throws IOException;

    String getType(); //Возвращает тип стратегии сериализации

    SerializationStrategy<String> STRING_SERIALIZATOR = new SerializationStrategy<String>() {
        @Override
        public void serializeToFile(String str, DataOutputStream output) throws IOException {
            output.writeUTF(str);
        }

        @Override
        public String deserializeFromFile(DataInputStream input) throws IOException {
            return input.readUTF();
        }

        @Override
        public String getType() {
            return "String";
        }
    };

    SerializationStrategy<Integer> INTEGER_SERIALIZATOR = new SerializationStrategy<Integer>() {
        @Override
        public void serializeToFile(Integer value, DataOutputStream output) throws IOException {
            output.writeInt(value);
        }

        @Override
        public Integer deserializeFromFile(DataInputStream input) throws IOException {
            return input.readInt();
        }

        @Override
        public String getType() {
            return "Integer";
        }
    };

    SerializationStrategy<Double> DOUBLE_SERIALIZATOR = new SerializationStrategy<Double>() {
        @Override
        public void serializeToFile(Double value, DataOutputStream output) throws IOException {
            output.writeDouble(value);
        }

        @Override
        public Double deserializeFromFile(DataInputStream input) throws IOException {
            return input.readDouble();
        }

        @Override
        public String getType() {
            return "Double";
        }
    };
}
