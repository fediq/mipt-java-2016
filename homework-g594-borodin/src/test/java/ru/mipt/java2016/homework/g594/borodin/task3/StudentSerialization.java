package ru.mipt.java2016.homework.g594.borodin.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

import ru.mipt.java2016.homework.g594.borodin.task3.SerializationStrategies.SerializationStrategy;
import ru.mipt.java2016.homework.tests.task2.Student;

/**
 * Created by Maxim on 10/31/2016.
 */
public class StudentSerialization implements SerializationStrategy<Student> {

	@Override
	public void serialize(Student value, DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(value.getGroupId());
		dataOutput.writeUTF(value.getName());
		dataOutput.writeUTF(value.getHometown());
		dataOutput.writeLong(value.getBirthDate().getTime());
		dataOutput.writeBoolean(value.isHasDormitory());
		dataOutput.writeDouble(value.getAverageScore());
	}

	@Override
	public Student deserialize(DataInput dataInput) throws IOException {
		int groupId = dataInput.readInt();
		String name = dataInput.readUTF();
		String hometown = dataInput.readUTF();
		Date birthDate = new Date(dataInput.readLong());
		boolean hasDormitory = dataInput.readBoolean();
		double averageScore = dataInput.readDouble();

		return new Student(groupId, name, hometown, birthDate, hasDormitory, averageScore);
	}
}
