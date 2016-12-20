package ru.mipt.java2016.homework.g594.glebov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * Created by daniil on 31.10.16.
 */
public interface MySerializer<Type> {

    void streamSerialize(Type object, DataOutputStream output) throws IOException;

    void streamSerialize(Type object, RandomAccessFile output) throws IOException;

    Type streamDeserialize(DataInputStream input) throws IOException;

    Type streamDeserialize(RandomAccessFile input) throws IOException;

    int typeSize();

    MySerializer<String> STRING = new MySerializer<String>() {
        @Override
        public void streamSerialize(String object, DataOutputStream output) throws IOException {
            output.writeUTF(object);
        }

        public void streamSerialize(String object, RandomAccessFile output) throws IOException {
            output.writeUTF(object);
        }


        @Override
        public String streamDeserialize(DataInputStream input) throws IOException {
            return input.readUTF();
        }

        @Override
        public String streamDeserialize(RandomAccessFile input) throws IOException {
            return input.readUTF();
        }


        @Override
        public int typeSize() {
            return STRING.typeSize();
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

        @Override
        public void streamSerialize(Integer object, RandomAccessFile output) throws IOException {
            output.writeInt(object);
        }

        @Override
        public Integer streamDeserialize(RandomAccessFile input) throws IOException {
            return input.readInt();
        }

        @Override
        public int typeSize() {
            return INT.typeSize();
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

        @Override
        public void streamSerialize(Double object, RandomAccessFile output) throws IOException {
            output.writeDouble(object);
        }

        @Override
        public Double streamDeserialize(RandomAccessFile input) throws IOException {
            return input.readDouble();
        }

        @Override
        public int typeSize() {
            return DOUBLE.typeSize();
        }
    };
}
