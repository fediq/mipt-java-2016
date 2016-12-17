package ru.mipt.java2016.homework.g595.zueva.task2.task2;

/**
 * Created by nestyme on 30.10.2016.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class Serializers {

    public static class SerialiserInt implements SerializerStorage<Integer> {


        public void writeToStream(DataOutputStream out, Integer value) throws IOException {

            out.writeInt(value);

        }

        public Integer readFromStream(DataInputStream in) throws IOException {

            return in.readInt();
        }
    }


    public static class SerializerStorageString implements SerializerStorage<String> {

        public void writeToStream(DataOutputStream out, String value) throws IOException {
            out.writeUTF(value);
        }

        public String readFromStream(DataInputStream in) throws IOException {
            return in.readUTF();
        }
    }

    public static class SerializerStorageDouble implements SerializerStorage<Double> {

        public void writeToStream(DataOutputStream out, Double value) throws IOException {
            out.writeDouble(value);
        }

        @Override
        public Double readFromStream(DataInputStream in) throws IOException {
            return in.readDouble();
        }
    }
}