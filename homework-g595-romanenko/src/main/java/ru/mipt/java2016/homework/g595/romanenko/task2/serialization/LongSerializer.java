package ru.mipt.java2016.homework.g595.romanenko.task2.serialization;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task2.serialization
 *
 * @author Ilya I. Romanenko
 * @since 26.10.16
 **/
public class LongSerializer extends SimpleIntegralTypeSerializer<Long> {

    private static final LongSerializer LONG_SERIALIZER = new LongSerializer();

    public static LongSerializer getInstance() {
        return LONG_SERIALIZER;
    }

    LongSerializer() {
        super.cntBYTES = Long.BYTES;
        super.binaryRepresentation = new byte[Long.BYTES];
    }

    protected Long getLongRepresentation(Long value) {
        return value;
    }

    protected Long convertFromLong(Long value) {
        return value;
    }
}