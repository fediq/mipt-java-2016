package ru.mipt.java2016.homework.g599.derut.task2;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DoubleRead  implements Serializer<Double> {

	@Override
	public void write(RandomAccessFile f, Double val) throws IOException {
		f.writeDouble(val);
	}

	@Override
	public Double read(RandomAccessFile f) throws IOException {
		return f.readDouble();
	}

}
