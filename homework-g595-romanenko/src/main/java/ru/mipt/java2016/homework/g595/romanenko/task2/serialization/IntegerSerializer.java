package ru.mipt.java2016.homework.g595.romanenko.task2.serialization;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task2.serialization
 *
 * @author Ilya I. Romanenko
 * @since 26.10.16
 **/
public class IntegerSerializer extends SimpleIntegralTypeSerializer<Integer> {

    private static final IntegerSerializer INTEGER_SERIALIZER = new IntegerSerializer();

    public static IntegerSerializer getInstance() {
        return INTEGER_SERIALIZER;
    }

    IntegerSerializer() {
        super.cntBYTES = Integer.BYTES;
        super.binaryRepresentation = new byte[Integer.BYTES];
    }

    protected Long getLongRepresentation(Integer value) {
        return value.longValue();
    }

    protected Integer convertFromLong(Long value) {
        return value.intValue();
    }

}