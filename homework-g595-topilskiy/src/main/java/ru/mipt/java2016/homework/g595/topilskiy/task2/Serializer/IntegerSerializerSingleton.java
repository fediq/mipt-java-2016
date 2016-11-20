package ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer;

import java.nio.ByteBuffer;

/**
 * Class for an Integer Serializer
 *
 * @author Artem K. Topilskiy
 * @since 30.10.16
 */
public class IntegerSerializerSingleton implements ISerializer<Integer> {
    /* The single allowed instance of a singleton class */
    private static IntegerSerializerSingleton instance;

    /* FORBID: direct instantiation of a singleton class */
    private IntegerSerializerSingleton() { }

    /**
     * Return (and create if needed) the only instance of this singleton
     *
     * @return a valid instance of the singleton
     */
    public static IntegerSerializerSingleton getInstance() {
        if (instance == null) {
            instance = new IntegerSerializerSingleton();
        }

        return instance;
    }


    /* Number of BYTES in the java class Integer */
    private static final int INTEGER_BYTE_SIZE = Integer.SIZE / Byte.SIZE;

    /**
     * Return the number of bytes in the bit-representation of Integer
     *
     * @return the number of bytes in Integer
     */
    public static int getIntegerByteSize() {
        return INTEGER_BYTE_SIZE;
    }


    /**
     * Serialize a Integer object into Bytes
     *
     * @param  value - object to be serialized
     * @return Array of Bytes, into which value has been serialized into
     */
    @Override
    public byte[] serialize(Integer value) {
        return ByteBuffer.allocate(INTEGER_BYTE_SIZE)
                         .putInt(value)
                         .array();
    }

    /**
     * Deserialize a Integer object from an Array of Bytes
     *
     * @param  valueBytes - Array of Bytes that contains an object of Integer
     * @return Deserialized value from valueBytes
     * @throws IllegalArgumentException if valueBytes cannot be converted to Integer
     */
    @Override
    public Integer deserialize(byte[] valueBytes) throws IllegalArgumentException {
        if (valueBytes.length != INTEGER_BYTE_SIZE) {
            throw new IllegalArgumentException("");
        } else {
            return ByteBuffer.wrap(valueBytes)
                             .getInt();
        }
    }
}
