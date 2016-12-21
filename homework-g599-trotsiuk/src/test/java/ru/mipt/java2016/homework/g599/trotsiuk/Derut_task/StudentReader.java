package ru.mipt.java2016.homework.g599.derut.task2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

public class StudentReader implements Serializer<Student> {
	@Override
	public void write(RandomAccessFile f, Student val) throws IOException {
		IntegerRead intS = new IntegerRead();
		StringReader stringS = new StringReader();
		DoubleRead dS = new DoubleRead();

		intS.write(f, val.getGroupId());
		stringS.write(f, val.getName());
		stringS.write(f, val.getHometown());
		f.writeLong(val.getBirthDate().getTime());
		boolean f1 = val.isHasDormitory();
		if (f1)
			intS.write(f, 1);
		else
			intS.write(f, 0);
		dS.write(f, val.getAverageScore());

	}

	@Override
	public Student read(RandomAccessFile f) throws IOException {
		IntegerRead intS = new IntegerRead();
		StringReader stringS = new StringReader();
		DoubleRead dS = new DoubleRead();
		int gID = intS.read(f);
		String name = stringS.read(f);
		String hmt = stringS.read(f);
		long time = f.readLong();
		Date d = new Date(time);
		int f1 = intS.read(f);
		boolean f2;
		if (f1 == 0)
			f2 = false;
		else
			f2 = true;
		double as = dS.read(f);
		return new Student(gID, name, hmt, d, f2, as);
	}

}
