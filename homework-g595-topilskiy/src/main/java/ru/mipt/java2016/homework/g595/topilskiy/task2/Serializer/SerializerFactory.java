package ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer;

import java.util.HashMap;

/**
 * Factory for Serializers
 *
 * @author Artem K. Topilskiy
 * @since 28.10.16
 */
public class SerializerFactory {
    /* A Cache HashMap by key valueOfSerialization for Serializers */
    private static final HashMap<String, ISerializer> CACHED_SERIALIZERS;

    static {
        CACHED_SERIALIZERS = new HashMap<>();
        CACHED_SERIALIZERS.put("Boolean",    new BooleanSerializer());
        CACHED_SERIALIZERS.put("Integer",    new IntegerSerializer());
        CACHED_SERIALIZERS.put("Long",       new LongSerializer());
        CACHED_SERIALIZERS.put("Double",     new DoubleSerializer());
        CACHED_SERIALIZERS.put("Date",       new DateSerializer());
        CACHED_SERIALIZERS.put("String",     new StringSerializer());
        CACHED_SERIALIZERS.put("StudentKey", new StudentKeySerializer());
        CACHED_SERIALIZERS.put("Student",    new StudentSerializer());
    }

    /**
     * Return the Serializer for valueOfSerialization
     *
     * @param  valueOfSerialization - type, whose Serializers should be returned
     * @return Serializer which serializes valueOfSerialization
     */
    public static ISerializer getSerializer(String valueOfSerialization) {
        return CACHED_SERIALIZERS.get(valueOfSerialization);
    }
}
