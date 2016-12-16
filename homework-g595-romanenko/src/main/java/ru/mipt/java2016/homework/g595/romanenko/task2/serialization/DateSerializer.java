package ru.mipt.java2016.homework.g595.romanenko.task2.serialization;

import java.util.Date;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task2.serialization
 *
 * @author Ilya I. Romanenko
 * @since 26.10.16
 **/
public class DateSerializer extends SimpleIntegralTypeSerializer<Date> {

    private static final DateSerializer DATE_SERIALIZER = new DateSerializer();

    public static DateSerializer getInstance() {
        return DATE_SERIALIZER;
    }

    DateSerializer() {
        super.cntBYTES = Long.BYTES;
        super.binaryRepresentation = new byte[Long.BYTES];
    }

    protected Long getLongRepresentation(Date value) {
        return value.getTime();
    }

    protected Date convertFromLong(Long value) {
        return new Date(value);
    }
}
