package ru.mipt.java2016.homework.g595.romanenko.task2.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractMap;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task2.serialization
 *
 * @author Ilya I. Romanenko
 * @since 30.10.16
 **/
public class MetaInformationSerializer<Meta, Value>
        implements SerializationStrategy<AbstractMap.SimpleEntry<Meta, Value>> {

    private SerializationStrategy<Meta> metaSerializer;
    private SerializationStrategy<Value> valueSerializer;

    public MetaInformationSerializer(SerializationStrategy<Meta> metaSerializer,
                                     SerializationStrategy<Value> valueSerializer) {
        this.valueSerializer = valueSerializer;
        this.metaSerializer = metaSerializer;
    }

    @Override
    public void serializeToStream(AbstractMap.SimpleEntry<Meta, Value> metaValuePair,
                                  OutputStream outputStream) throws IOException {
        metaSerializer.serializeToStream(metaValuePair.getKey(), outputStream);
        valueSerializer.serializeToStream(metaValuePair.getValue(), outputStream);
    }

    @Override
    public int getBytesSize(AbstractMap.SimpleEntry<Meta, Value> metaValuePair) {
        return metaSerializer.getBytesSize(metaValuePair.getKey()) +
                valueSerializer.getBytesSize(metaValuePair.getValue());
    }

    @Override
    public AbstractMap.SimpleEntry<Meta, Value> deserializeFromStream(InputStream inputStream)
            throws IOException {
        Meta metaResult = metaSerializer.deserializeFromStream(inputStream);
        Value result = valueSerializer.deserializeFromStream(inputStream);
        return new AbstractMap.SimpleEntry<>(metaResult, result);
    }
}
