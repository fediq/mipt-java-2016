package ru.mipt.java2016.homework.g595.romanenko.task2.serialization;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task2.serialization
 *
 * @author Ilya I. Romanenko
 * @since 30.10.16
 **/
public class ByteSerializer extends SimpleIntegralTypeSerializer<Byte> {

    private static final ByteSerializer BYTE_SERIALIZER = new ByteSerializer();

    public static ByteSerializer getInstance() {
        return BYTE_SERIALIZER;
    }

    ByteSerializer() {
        super.cntBYTES = Byte.BYTES;
        super.binaryRepresentation = new byte[Byte.BYTES];
    }

    protected Long getLongRepresentation(Byte value) {
        return value.longValue();
    }

    protected Byte convertFromLong(Long value) {
        return value.byteValue();
    }
}
