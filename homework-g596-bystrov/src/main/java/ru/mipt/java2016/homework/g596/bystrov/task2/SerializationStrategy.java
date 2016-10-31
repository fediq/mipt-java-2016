package ru.mipt.java2016.homework.g596.bystrov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by AlexBystrov.
 */
public interface SerializationStrategy<V> {
    void serialize(V value, DataOutputStream out) throws IOException;

    V deserialize(DataInputStream in) throws IOException;

    SerializationStrategy<Integer> SS_INT = new SerializationStrategy<Integer>() {
        @Override
        public void serialize(Integer x, DataOutputStream out) throws IOException {
            out.writeInt(x);
        }

        @Override
        public Integer deserialize(DataInputStream in) throws IOException {
            return in.readInt();
        }
    };

    SerializationStrategy<Double> SS_DOUBLE = new SerializationStrategy<Double>() {
        @Override
        public void serialize(Double x, DataOutputStream out) throws IOException {
            out.writeDouble(x);
        }

        @Override
        public Double deserialize(DataInputStream in) throws IOException {
            return in.readDouble();
        }
    };

    SerializationStrategy<String> SS_STRING = new SerializationStrategy<String>() {
        @Override
        public void serialize(String x, DataOutputStream out) throws IOException {
            out.writeUTF(x);
        }

        @Override
        public String deserialize(DataInputStream in) throws IOException {
            return in.readUTF();
        }
    };

}
