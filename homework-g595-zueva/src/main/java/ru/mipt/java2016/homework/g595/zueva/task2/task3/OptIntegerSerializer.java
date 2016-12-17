package ru.mipt.java2016.homework.g595.zueva.task2.task3;
import java.nio.ByteBuffer;
public class OptIntegerSerializer implements OptKVStorageSerializer<Integer> {
       @Override
     public ByteBuffer srlzToStr(Integer value) {
                ByteBuffer serialized = ByteBuffer.allocate(SrlzSize(value));
                serialized.putInt(value);
                return serialized;
            }

             @Override
     public Integer desrlzFrStr(ByteBuffer input) {
                return input.getInt();
            }

             @Override
     public int SrlzSize(Integer value) {
               return Integer.SIZE / 8;
            }
}
