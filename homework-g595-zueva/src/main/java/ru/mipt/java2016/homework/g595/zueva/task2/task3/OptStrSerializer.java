package ru.mipt.java2016.homework.g595.zueva.task2.task3;
import java.nio.ByteBuffer;
public class OptStrSerializer implements OptKVStorageSerializer<String> {
        @Override
     public ByteBuffer SerialToStrm(String value) {
                return ByteBuffer.wrap(value.getBytes());
            }

             @Override
     public String DeserialFromStrm(ByteBuffer input) {
                return new String(input.array());
            }

             @Override
     public int Size(String data) {
                return data.length();
            }
}
