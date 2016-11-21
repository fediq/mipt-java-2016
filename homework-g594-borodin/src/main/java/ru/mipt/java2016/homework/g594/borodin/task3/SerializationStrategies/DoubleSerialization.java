package ru.mipt.java2016.homework.g594.borodin.task3.SerializationStrategies;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Maxim on 10/31/2016.
 */
public class DoubleSerialization implements  SerializationStrategy<Double> {

	@Override
	public void serialize(Double value, DataOutput dataOutput) throws IOException {
		dataOutput.writeDouble(value);
	}

	@Override
	public Double deserialize(DataInput dataInput) throws IOException {
		return dataInput.readDouble();
	}
}
