package ru.mipt.java2016.homework.g595.romanenko.task2.serialization;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task2.serialization
 *
 * @author Ilya I. Romanenko
 * @since 26.10.16
 **/
public class DoubleSerializer extends SimpleIntegralTypeSerializer<Double> {

    private static final DoubleSerializer DOUBLE_SERIALIZER = new DoubleSerializer();

    public static DoubleSerializer getInstance() {
        return DOUBLE_SERIALIZER;
    }

    DoubleSerializer() {
        super.cntBYTES = Double.BYTES;
        super.binaryRepresentation = new byte[Double.BYTES];
    }

    protected Long getLongRepresentation(Double value) {
        return Double.doubleToLongBits(value);
    }

    protected Double convertFromLong(Long value) {
        return Double.longBitsToDouble(value);
    }
}
