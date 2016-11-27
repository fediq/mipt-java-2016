package ru.mipt.java2016.homework.g595.manucharyan.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Vardan Manucharyan
 * @since 30.10.16
 */
public class ConcreteStrategyStringRandomAccess implements SerializationStrategyRandomAccess<String> {
    @Override
    public void serializeToFile(String value, DataOutput output) throws IOException {
        output.writeUTF(value);
    }

    @Override
    public String deserializeFromFile(DataInput input) throws IOException {
        return input.readUTF();
    }

}
