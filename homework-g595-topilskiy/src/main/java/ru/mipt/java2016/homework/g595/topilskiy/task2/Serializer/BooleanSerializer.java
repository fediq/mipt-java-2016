package ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer;

/**
 * Class for an Boolean Serializer
 *
 * @author Artem K. Topilskiy
 * @since 30.10.16
 */
public class BooleanSerializer implements ISerializer<Boolean> {
    /* Number of BYTES in the java class Boolean */
    private static final int BOOLEAN_BYTE_SIZE = Byte.SIZE / Byte.SIZE;
    /* Wrappers for (byte) to use for Boolean serialization and deserialization */
    private static final byte BOOLEAN_BYTE_FALSE = (byte) 0;
    private static final byte BOOLEAN_BYTE_TRUE  = (byte) 1;

    /**
     * Return the number of bytes in the bit-representation of Boolean
     *
     * @return the number of bytes in Boolean
     */
    public static int getBooleanByteSize() {
        return BOOLEAN_BYTE_SIZE;
    }

    /**
     * Serialize a Boolean object into Bytes
     *
     * @param  value - object to be serialized
     * @return Array of Bytes, into which value has been serialized into
     */
    @Override
    public byte[] serialize(Boolean value) {
        byte[] valueBytes = new byte[1];

        if (value) {
            valueBytes[0] = BOOLEAN_BYTE_TRUE;
        } else {
            valueBytes[0] = BOOLEAN_BYTE_FALSE;
        }

        return valueBytes;
    }

    /**
     * Deserialize a Boolean object from an Array of Bytes
     *
     * @param  valueBytes - Array of Bytes that contains an object of Boolean
     * @return Deserialized value from valueBytes
     * @throws IllegalArgumentException if valueBytes cannot be converted to Boolean
     */
    @Override
    public Boolean deserialize(byte[] valueBytes) throws IllegalArgumentException {
        if (valueBytes.length != BOOLEAN_BYTE_SIZE) {
            throw new IllegalArgumentException("");
        } else {
            return valueBytes[0] == BOOLEAN_BYTE_TRUE;
        }
    }
}
