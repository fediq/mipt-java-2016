package ru.mipt.java2016.homework.g595.murzin.task3fast;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Дмитрий Мурзин on 24.10.16.
 */
public interface SerializationStrategy<Value> {

    /**
     * Записать сериализованное значение в поток
     */
    void serializeToStream(Value value, DataOutput output) throws IOException;

    /**
     * Прочесть сериализованное значение из текущего места в потоке
     */
    Value deserializeFromStream(DataInput input) throws IOException;

    SerializationStrategy<String> FOR_STRING = new SerializationStrategy<String>() {
        private byte[] bytearr;

        @Override
        public void serializeToStream(String s, DataOutput output) throws IOException {
            output.writeUTF(s);
        }

        @Override
        public String deserializeFromStream(DataInput input) throws IOException {
            return input.readUTF();
        }
    };

    SerializationStrategy<Integer> FOR_INTEGER = new SerializationStrategy<Integer>() {
        @Override
        public void serializeToStream(Integer integer, DataOutput output) throws IOException {
            output.writeInt(integer);
        }

        @Override
        public Integer deserializeFromStream(DataInput input) throws IOException {
            return input.readInt();
        }
    };

    SerializationStrategy<Double> FOR_DOUBLE = new SerializationStrategy<Double>() {
        @Override
        public void serializeToStream(Double value, DataOutput output) throws IOException {
            output.writeDouble(value);
        }

        @Override
        public Double deserializeFromStream(DataInput input) throws IOException {
            return input.readDouble();
        }
    };
}
