package ru.mipt.java2016.homework.g594.borodin.task3.SerializationStrategies;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Maxim on 11/20/2016.
 */
public class IntSerialization implements SerializationStrategy<Integer> {

	@Override
	public void serialize(Integer value, DataOutput dataOutputStream) throws IOException {
		dataOutputStream.writeInt(value);
	}

	@Override
	public Integer deserialize(DataInput dataInputStream) throws IOException {
		return dataInputStream.readInt();
	}
}
