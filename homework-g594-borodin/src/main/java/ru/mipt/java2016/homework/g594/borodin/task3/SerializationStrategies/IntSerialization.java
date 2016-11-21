package ru.mipt.java2016.homework.g594.borodin.task3.SerializationStrategies;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Maxim on 11/20/2016.
 */
public class IntSerialization implements SerializationStrategy<Integer> {

		@Override
		public void serialize(Integer value, DataOutput dataOutput) throws IOException {
			dataOutput.writeInt(value);
		}

		@Override
		public Integer deserialize(DataInput dataInput) throws IOException {
			return dataInput.readInt();
		}
	}
