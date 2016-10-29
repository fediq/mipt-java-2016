package ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer;

import ru.mipt.java2016.homework.g595.topilskiy.task2.JoinArrays.JoinArraysByte;

/**
 * Interface for an String Serializer
 *
 * @author Artem K. Topilskiy
 * @since 28.10.16
 */
public class StringSerializer implements ISerializer<String> {
    /* Number of BYTES in the java class String */
    private static final int INTEGER_BYTE_SIZE = Integer.SIZE / Byte.SIZE;

    /**
     * Serialize a String object into Bytes
     *
     * @param  value - object to be serialized
     * @return Array of Bytes, into which value has been serialized into
     */
    public byte[] serialize(String value) {
        byte[] lenBytes;
        byte[] valueBytes;
        return null;
    }

    /**
     * Deserialize a String object from an Array of Bytes
     *
     * @param  valueBytes - Array of Bytes that contains an object of String
     * @return Deserialized value from valueBytes
     * @throws IllegalArgumentException if valueBytes cannot be converted to String
     */
    public String deserialize(byte[] valueBytes) throws IllegalArgumentException {
        return null;
    }
}
