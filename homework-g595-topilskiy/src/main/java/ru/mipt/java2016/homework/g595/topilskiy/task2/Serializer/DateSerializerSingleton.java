package ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer;

import java.util.Date;

/**
 * Class for an Date Serializer
 *
 * @author Artem K. Topilskiy
 * @since 30.10.16
 */
public class DateSerializerSingleton implements ISerializer<Date> {
    /* The single allowed instance of a singleton class */
    private static DateSerializerSingleton instance;

    /* FORBID: direct instantiation of a singleton class */
    private DateSerializerSingleton() { }

    /**
     * Return (and create if needed) the only instance of this singleton
     *
     * @return a valid instance of the singleton
     */
    public static DateSerializerSingleton getInstance() {
        if (instance == null) {
            instance = new DateSerializerSingleton();
        }

        return instance;
    }


    /* A LongSerializer for serializing and deserializing within the class */
    private static final LongSerializerSingleton LONG_SERIALIZER = LongSerializerSingleton.getInstance();


    /**
     * Return the number of bytes in the bit-representation of Date
     *
     * @return the number of bytes in Date
     */
    public static int getDateByteSize() {
        return LongSerializerSingleton.getLongByteSize();
    }

    /**
     * Serialize a Date object into Bytes
     *
     * @param  value - object to be serialized
     * @return Array of Bytes, into which value has been serialized into
     */
    @Override
    public byte[] serialize(Date value) {
        return LONG_SERIALIZER.serialize(value.getTime());
    }

    /**
     * Deserialize a Date object from an Array of Bytes
     *
     * @param  valueBytes - Array of Bytes that contains an object of Date
     * @return Deserialized value from valueBytes
     * @throws IllegalArgumentException if valueBytes cannot be converted to Date
     */
    @Override
    public Date deserialize(byte[] valueBytes) throws IllegalArgumentException {
        return new Date(LONG_SERIALIZER.deserialize(valueBytes));
    }
}
