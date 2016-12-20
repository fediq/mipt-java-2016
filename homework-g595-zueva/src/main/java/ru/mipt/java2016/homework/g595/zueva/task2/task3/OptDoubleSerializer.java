package ru.mipt.java2016.homework.g595.zueva.task2.task3;


import java.nio.ByteBuffer;

         public class OptDoubleSerializer implements OptKVStorageSerializer<Double> {

            @Override
    public ByteBuffer srlzToStr(Double value) {
                ByteBuffer serialized = ByteBuffer.allocate(SrlzSize(value));
                serialized.putDouble(value);
                return serialized;
            }

             @Override
     public Double desrlzFrStr(ByteBuffer input) {
                return input.getDouble();
            }

             @Override
     public int SrlzSize(Double value) {
                return Double.SIZE / 8;
            }
 }
