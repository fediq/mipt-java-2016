package ru.mipt.java2016.homework.g595.topilskiy.task3.Serializer;

import ru.mipt.java2016.homework.g595.topilskiy.task2.JoinArrays.JoinArraysPrimitiveByte;
import ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Date;

/**
 * Class for reading/writing serialized values to a stream
 *
 * @author Artem K. Topilskiy
 * @since 21.11.16
 */
public class RWStreamSerializer {
    /* Integer length symbolising that the value being read is actually null */
    private static final Integer NULL_INTEGER_LENGTH = 0;

    /**
     * Serialize a Type object into outStream
     *
     * @param value - object to be serialized
     * @param typeSerializer - the serializer of value
     * @param outStream - OutputStream to be written to
     * @throws IOException - if data cannot be written properly
     */
    public static <Type> void serialize(Type value, ISerializer typeSerializer,
                                        OutputStream outStream) throws IOException {
        outStream.write(getNumBytesAndBytes(value, typeSerializer));
    }

    /**
     * Serialize value with an extra length prefix for future linear reading
     *
     * @param value - element to be extra serialized (with length prefix)
     * @param typeSerializer - the serializer of value
     * @param <Type> - Type of the value
     * @return NumBytes + Value in binary form
     */
    private static <Type> byte[] getNumBytesAndBytes(Type value, ISerializer typeSerializer) {
        if (value == null) {
            return IntegerSerializerSingleton.getInstance().serialize(NULL_INTEGER_LENGTH);
        } else {
            byte[] valueBytes = typeSerializer.serialize(value);
            byte[] valueNumBytes = IntegerSerializerSingleton.getInstance().serialize(valueBytes.length);
            return JoinArraysPrimitiveByte.joinArrays(valueNumBytes, valueBytes);
        }
    }
    
    /* Wrapping of serialization for known types */
    public static void serializeBoolean(Boolean value, OutputStream outStream) throws IOException {
        serialize(value, BooleanSerializerSingleton.getInstance(), outStream);
    }

    public static void serializeDate(Date value, OutputStream outStream) throws IOException {
        serialize(value, DateSerializerSingleton.getInstance(), outStream);
    }

    public static void serializeDouble(Double value, OutputStream outStream) throws IOException {
        serialize(value, DoubleSerializerSingleton.getInstance(), outStream);
    }

    public static void serializeInteger(Integer value, OutputStream outStream) throws IOException {
        serialize(value, IntegerSerializerSingleton.getInstance(), outStream);
    }

    public static void serializeLong(Long value, OutputStream outStream) throws IOException {
        serialize(value, LongSerializerSingleton.getInstance(), outStream);
    }

    public static void serializeString(String value, OutputStream outStream) throws IOException {
        serialize(value, StringSerializerSingleton.getInstance(), outStream);
    }



    /**
     * Deserialize a Type value from inStream
     *
     * @param  readTypeSerializer - the Serializer of Type to be read
     * @param  inStream - InputStream to be read from
     * @return an Object read, which can be casted to Type
     * @throws IOException - if data cannot be read properly
     */
    public static Object deserialize(ISerializer readTypeSerializer, InputStream inStream) throws IOException {
        Integer lenRead = readInteger(inStream);

        if (lenRead.equals(NULL_INTEGER_LENGTH)) {
            return null;
        } else {
            byte[] typeBytes = new byte[lenRead];
            inStream.read(typeBytes);

            return readTypeSerializer.deserialize(typeBytes);
        }
    }

    /**
     * Deserialize an Integer value from inStream
     *
     * @param  inStream - inStream to be read from
     * @return the read Integer
     * @throws IOException - if file cannot be read properly
     */
    private static Integer readInteger(InputStream inStream) throws IOException {
        byte[] integerBytes = new byte[IntegerSerializerSingleton.getIntegerByteSize()];
        inStream.read(integerBytes);
        return IntegerSerializerSingleton.getInstance().deserialize(integerBytes);
    }

    /* Wrapping of deserialization for known types */
    public static Boolean deserializeBoolean(InputStream inStream) throws IOException {
        return (Boolean) deserialize(BooleanSerializerSingleton.getInstance(), inStream);
    }

    public static Date deserializeDate(InputStream inStream) throws IOException {
        return (Date) deserialize(DateSerializerSingleton.getInstance(), inStream);
    }

    public static Double deserializeDouble(InputStream inStream) throws IOException {
        return (Double) deserialize(DoubleSerializerSingleton.getInstance(), inStream);
    }

    public static Integer deserializeInteger(InputStream inStream) throws IOException {
        return (Integer) deserialize(IntegerSerializerSingleton.getInstance(), inStream);
    }

    public static Long deserializeLong(InputStream inStream) throws IOException {
        return (Long) deserialize(LongSerializerSingleton.getInstance(), inStream);
    }

    public static String deserializeString(InputStream inStream) throws IOException {
        return (String) deserialize(StringSerializerSingleton.getInstance(), inStream);
    }
}
