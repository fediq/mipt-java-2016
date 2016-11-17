package ru.mipt.java2016.homework.g594.sharuev.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StringSerializer implements SerializationStrategy<String> {
    @Override
    public void serializeToStream(String s,
                                  DataOutputStream outputStream) throws SerializationException {
        try {
            outputStream.writeUTF(s);
            /*byte[] bytes = s.getBytes("UTF-8");
            outputStream.writeInt(s.length());
            //outputStream.writeChars(s);
            outputStream.write(bytes);*/
        } catch (IOException e) {
            throw new SerializationException("String serialization error", e);
        }
    }

    @Override
    public String deserializeFromStream(DataInputStream inputStream) throws SerializationException {
        try {
            return inputStream.readUTF();

            /*char[] chars = new char[len];
            for (int i = 0 ; i < len ; i++) {
                char c = 0;
                byte b1 = inputStream.readByte();
                byte b2 = inputStream.readByte();
                c =(char) ((b2 >>> 0) | (b1 >>> 8)) ;
                chars[i] = c;
            }*/
            /*byte[] bytes = new byte[len];
            if (inputStream.read(bytes) != len) {
                throw new SerializationException("Not all data was read");
            }return new String(bytes);*/
            //return new String(chars);
            /*int len = inputStream.readInt();
            byte[] bytes = new byte[len];
            inputStream.read(bytes);
            return new String(bytes);*/
        } catch (IOException e) {
            throw new SerializationException("String deserialization error", e);
        }
    }

    @Override
    public Class getSerializingClass() {
        return String.class;
    }
}
