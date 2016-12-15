package ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer;

import ru.mipt.java2016.homework.g595.topilskiy.task2.JoinArrays.JoinArraysPrimitiveByte;

import static java.util.Arrays.copyOfRange;

/**
 * Class for a String Serializer
 *
 * @author Artem K. Topilskiy
 * @since 30.10.16
 */
public class StringSerializerSingleton implements ISerializer<String> {
    /* The single allowed instance of a singleton class */
    private static StringSerializerSingleton instance;

    /* FORBID: direct instantiation of a singleton class */
    private StringSerializerSingleton() { }

    /**
     * Return (and create if needed) the only instance of this singleton
     *
     * @return a valid instance of the singleton
     */
    public static StringSerializerSingleton getInstance() {
        if (instance == null) {
            instance = new StringSerializerSingleton();
        }

        return instance;
    }


    /* An IntegerSerializer for serializing and deserializing within the class */
    private static final IntegerSerializerSingleton INTEGER_SERIALIZER =
                         IntegerSerializerSingleton.getInstance();

    /**
     * Serialize a String object into Bytes
     *
     * @param  value - object to be serialized
     * @return Array of Bytes, into which value has been serialized into
     */
    @Override
    public byte[] serialize(String value) {
        byte[] lenBytes = INTEGER_SERIALIZER.serialize(value.length());
        byte[] valueBytes = value.getBytes();
        return JoinArraysPrimitiveByte.joinArrays(lenBytes, valueBytes);
    }

    /**
     * Deserialize a String object from an Array of Bytes
     *
     * @param  valueBytes - Array of Bytes that contains an object of String
     * @return Deserialized value from valueBytes
     * @throws IllegalArgumentException if valueBytes cannot be converted to String
     */
    @Override
    public String deserialize(byte[] valueBytes) throws IllegalArgumentException {
        if (valueBytes.length < IntegerSerializerSingleton.getIntegerByteSize()) {
            throw new IllegalArgumentException("Illegal Deserialization");
        }

        int stringSize = INTEGER_SERIALIZER.deserialize(
                copyOfRange(valueBytes, 0, IntegerSerializerSingleton.getIntegerByteSize()));

        if (IntegerSerializerSingleton.getIntegerByteSize() + stringSize != valueBytes.length) {
            throw new IllegalArgumentException("Illegal Deserialization");
        }

        return new String(
                copyOfRange(valueBytes, IntegerSerializerSingleton.getIntegerByteSize(), valueBytes.length));
    }
}
