package ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer;

import java.nio.ByteBuffer;

/**
 * Class for an Long Serializer
 *
 * @author Artem K. Topilskiy
 * @since 30.10.16
 */
public class LongSerializerSingleton implements ISerializer<Long> {
    /* The single allowed instance of a singleton class */
    private static LongSerializerSingleton instance;

    /* FORBID: direct instantiation of a singleton class */
    private LongSerializerSingleton() { }

    /**
     * Return (and create if needed) the only instance of this singleton
     *
     * @return a valid instance of the singleton
     */
    public static LongSerializerSingleton getInstance() {
        if (instance == null) {
            instance = new LongSerializerSingleton();
        }

        return instance;
    }


    /* Number of BYTES in the java class Long */
    private static final int LONG_BYTE_SIZE = Long.SIZE / Byte.SIZE;

    /**
     * Return the number of bytes in the bit-representation of Long
     *
     * @return the number of bytes in Long
     */
    public static int getLongByteSize() {
        return LONG_BYTE_SIZE;
    }

    /**
     * Serialize a Long object into Bytes
     *
     * @param  value - object to be serialized
     * @return Array of Bytes, into which value has been serialized into
     */
    @Override
    public byte[] serialize(Long value) {
        return ByteBuffer.allocate(LONG_BYTE_SIZE)
                         .putLong(value)
                         .array();
    }

    /**
     * Deserialize a Long object from an Array of Bytes
     *
     * @param  valueBytes - Array of Bytes that contains an object of Long
     * @return Deserialized value from valueBytes
     * @throws IllegalArgumentException if valueBytes cannot be converted to Long
     */
    @Override
    public Long deserialize(byte[] valueBytes) throws IllegalArgumentException {
        if (valueBytes.length != LONG_BYTE_SIZE) {
            throw new IllegalArgumentException("");
        } else {
            return ByteBuffer.wrap(valueBytes)
                             .getLong();
        }
    }
}
