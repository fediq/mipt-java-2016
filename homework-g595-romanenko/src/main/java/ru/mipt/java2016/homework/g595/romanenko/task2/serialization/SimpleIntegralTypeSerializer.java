package ru.mipt.java2016.homework.g595.romanenko.task2.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task2.serialization
 *
 * @author Ilya I. Romanenko
 * @since 26.10.16
 **/
public abstract class SimpleIntegralTypeSerializer<IntegralType>
        implements SerializationStrategy<IntegralType> {

    private static int getAbsoluteValueOfByte(byte value) {
        return (value + 256) % 256;
    }

    protected int cntBYTES = 0;
    protected byte[] binaryRepresentation = null;

    protected Long getLongRepresentation(IntegralType value) {
        return 0L;
    }

    protected IntegralType convertFromLong(Long value) {
        return null;
    }

    @Override
    public void serializeToStream(IntegralType value, OutputStream outputStream) throws IOException {
        Long longValue = getLongRepresentation(value);
        for (int i = 0; i < cntBYTES; i++) {
            binaryRepresentation[cntBYTES - i - 1] = longValue.byteValue();
            longValue >>= 8;
        }
        outputStream.write(binaryRepresentation);
    }

    @Override
    public int getBytesSize(IntegralType value) {
        return cntBYTES;
    }

    @Override
    public IntegralType deserializeFromStream(InputStream inputStream) throws IOException {
        inputStream.read(binaryRepresentation);
        long value = 0;
        for (int i = 0; i < cntBYTES; i++) {
            value = (value << 8) + getAbsoluteValueOfByte(binaryRepresentation[i]);
        }
        return convertFromLong(value);
    }

    @Override
    public byte[] readValueAsBytes(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[cntBYTES];
        inputStream.read(bytes);
        return bytes;
    }
}
