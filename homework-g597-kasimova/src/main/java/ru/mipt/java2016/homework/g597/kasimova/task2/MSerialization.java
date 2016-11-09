package ru.mipt.java2016.homework.g597.kasimova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Надежда on 29.10.2016.
 */

public interface MSerialization<Type> {
    void serializeToStream(Type value, DataOutputStream outStream);

    Type deserializeFromStream(DataInputStream inStream);

    MSerialization<String> STRING_SERIALIZER = new MSerialization<String>() {
        @Override
        public void serializeToStream(String value, DataOutputStream outStream) {
            try {
                outStream.writeUTF(value);
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
        }

        @Override
        public String deserializeFromStream(DataInputStream inStream) {
            try {
                return inStream.readUTF();
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
            return null;
        }
    };

    MSerialization<Integer> INTEGER_SERIALIZER = new MSerialization<Integer>() {
        @Override
        public void serializeToStream(Integer value, DataOutputStream outStream) {
            try {
                outStream.writeInt(value);
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
        }

        @Override
        public Integer deserializeFromStream(DataInputStream inStream) {
            try {
                return inStream.readInt();
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
            return null;
        }
    };

    MSerialization<Double> DOUBLE_SERIALIZER = new MSerialization<Double>() {
        @Override
        public void serializeToStream(Double value, DataOutputStream outStream) {
            try {
                outStream.writeDouble(value);
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
        }

        @Override
        public Double deserializeFromStream(DataInputStream inStream) {
            try {
                return inStream.readDouble();
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
            return null;
        }
    };
}