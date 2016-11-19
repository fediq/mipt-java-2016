package ru.mipt.java2016.homework.g595.zueva.task2;

/**
 * Created by nestyme on 30.10.2016.
 */

import java.io.*;
import java.util.Date;


public class Serializers {

    public static class SerialiserInt implements Serializer<Integer> {


        public void writeToStream(DataOutputStream out, Integer value) throws IOException {

            out.writeInt(value);

        }

        public Integer readFromStream(DataInputStream in) throws IOException {

            return in.readInt();
        }
    }


    public static class SerializerString implements Serializer<String> {

        public void writeToStream(DataOutputStream out, String value) throws IOException {
            out.writeUTF(value);
        }

        public String readFromStream(DataInputStream in) throws IOException {
            return in.readUTF();
        }
    }

    public static class SerializerDouble implements Serializer<Double> {

        public void writeToStream(DataOutputStream out, Double value) throws IOException {
            out.writeDouble(value);
        }

        @Override
        public Double readFromStream(DataInputStream in) throws IOException {
            return in.readDouble();
        }
    }
}