package ru.mipt.java2016.homework.g597.kasimova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Надежда on 29.10.2016.
 */

public interface MSerialization<Type> {
    void serializeToStream(Type value, DataOutput outStream);

    Type deserializeFromStream(DataInput inStream);

    MSerialization<String> STRING_SERIALIZER = new MSerialization<String>() {
        @Override
        public void serializeToStream(String value, DataOutput outStream) {
            try {
                outStream.writeUTF(value);
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
        }

        @Override
        public String deserializeFromStream(DataInput inStream) {
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
        public void serializeToStream(Integer value, DataOutput outStream) {
            try {
                outStream.writeInt(value);
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
        }

        @Override
        public Integer deserializeFromStream(DataInput inStream) {
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
        public void serializeToStream(Double value, DataOutput outStream) {
            try {
                outStream.writeDouble(value);
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
        }

        @Override
        public Double deserializeFromStream(DataInput inStream) {
            try {
                return inStream.readDouble();
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
            return null;
        }
    };

    MSerialization<Boolean> BOOLEAN_SERIALIZER = new MSerialization<Boolean>() {
        @Override
        public void serializeToStream(Boolean value, DataOutput outStream) {
            try {
                outStream.writeBoolean(value);
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
        }

        @Override
        public Boolean deserializeFromStream(DataInput inStream) {
            try {
                return inStream.readBoolean();
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
            return null;
        }
    };

    MSerialization<Long> LONG_SERIALIZER = new MSerialization<Long>() {
        @Override
        public void serializeToStream(Long value, DataOutput outStream) {
            try {
                outStream.writeLong(value);
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
        }

        @Override
        public Long deserializeFromStream(DataInput inStream) {
            try {
                return inStream.readLong();
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
            return null;
        }
    };
}