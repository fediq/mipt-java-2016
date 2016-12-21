package ru.mipt.java2016.homework.g599.derut.task2;
import java.io.IOException;
import java.io.RandomAccessFile;

public class IntegerRead implements Serializer<Integer> {

	@Override
	public void write(RandomAccessFile f,Integer val) throws IOException {
		f.writeInt(val);
	}

	@Override
	public Integer read(RandomAccessFile f) throws IOException {	
		return f.readInt();
	}

}
