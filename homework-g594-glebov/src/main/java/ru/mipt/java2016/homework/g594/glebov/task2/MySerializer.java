package ru.mipt.java2016.homework.g594.glebov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * Created by daniil on 31.10.16.
 */
public interface MySerializer<Type> {

    void streamSerialize(Type object, DataOutputStream output) throws IOException;

    Type streamDeserialize(DataInputStream input) throws IOException;

    MySerializer<String> STRING = new MySerializer<String>() {
        @Override
        public void streamSerialize(String object, DataOutputStream output) throws IOException {
            output.writeUTF(object);
        }

        @Override
        public String streamDeserialize(DataInputStream input) throws IOException {
            return input.readUTF();
        }
    };

    MySerializer<Integer> INT = new MySerializer<Integer>() {
        @Override
        public void streamSerialize(Integer object, DataOutputStream output) throws IOException {
            output.writeInt(object);
        }

        @Override
        public Integer streamDeserialize(DataInputStream input) throws IOException {
            return input.readInt();
        }
    };

    MySerializer<Double> DOUBLE = new MySerializer<Double>() {
        @Override
        public void streamSerialize(Double object, DataOutputStream output) throws IOException {
            output.writeDouble(object);
        }

        @Override
        public Double streamDeserialize(DataInputStream input) throws IOException {
            return input.readDouble();
        }
    };
}
