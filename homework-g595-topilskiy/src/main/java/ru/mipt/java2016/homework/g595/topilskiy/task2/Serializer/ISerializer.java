package ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer;

/**
 * Interface for a ValueType Serializer
 *
 * @author Artem K. Topilskiy
 * @since 28.10.16
 */
public interface ISerializer<ValueType> {
    /**
     * Serialize a ValueType object into Bytes
     *
     * @param  value - object to be serialized
     * @return Array of Bytes, into which value has been serialized into
     */
    byte[] serialize(ValueType value);

    /**
     * Deserialize a ValueType object from an Array of Bytes
     *
     * @param  valueBytes - Array of Bytes that contains an object of ValueType
     * @return Deserialized value from valueBytes
     * @throws IllegalArgumentException if valueBytes cannot be converted to ValueType
     */
    ValueType deserialize(byte[] valueBytes) throws IllegalArgumentException;
}
