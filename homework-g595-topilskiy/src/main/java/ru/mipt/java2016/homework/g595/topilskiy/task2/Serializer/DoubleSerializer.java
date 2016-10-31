package ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer;

import java.nio.ByteBuffer;

/**
 * Class for a Double Serializer
 *
 * @author Artem K. Topilskiy
 * @since 30.10.16
 */
public class DoubleSerializer implements ISerializer<Double> {
    /* Number of BYTES in the java class Double */
    private static final int DOUBLE_BYTE_SIZE = Double.SIZE / Byte.SIZE;

    /**
     * Return the number of bytes in the bit-representation of Double
     *
     * @return the number of bytes in Double
     */
    public static int getDoubleByteSize() {
        return DOUBLE_BYTE_SIZE;
    }

    /**
     * Serialize a Double object into Bytes
     *
     * @param  value - object to be serialized
     * @return Array of Bytes, into which value has been serialized into
     */
    @Override
    public byte[] serialize(Double value) {
        return ByteBuffer.allocate(DOUBLE_BYTE_SIZE)
                         .putDouble(value)
                         .array();
    }

    /**
     * Deserialize a Double object from an Array of Bytes
     *
     * @param  valueBytes - Array of Bytes that contains an object of Double
     * @return Deserialized value from valueBytes
     * @throws IllegalArgumentException if valueBytes cannot be converted to Double
     */
    @Override
    public Double deserialize(byte[] valueBytes) throws IllegalArgumentException {
        if (valueBytes.length != DOUBLE_BYTE_SIZE) {
            throw new IllegalArgumentException("");
        } else {
            return ByteBuffer.wrap(valueBytes)
                             .getDouble();
        }
    }
}
