package ru.mipt.java2016.homework.g597.markov.task2;

/**
 * Created by Alexander on 30.10.2016.
 */

import ru.mipt.java2016.homework.g597.markov.task2.IntegerSerializator;
import ru.mipt.java2016.homework.g597.markov.task2.SerializationStrategy;
import ru.mipt.java2016.homework.g597.markov.task2.StringSerializator;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;

public class StudentKeySerializator implements SerializationStrategy<StudentKey> {

    @Override
    public StudentKey read(RandomAccessFile fileName) throws IOException{
        Integer groupId = integerSerializator.read(fileName);
        String name = stringSerializator.read(fileName);
        return new StudentKey(groupId, name);
    }

    @Override
    public void write(RandomAccessFile fileName, StudentKey studentKey) throws IOException{
        integerSerializator.write(fileName, studentKey.getGroupId());
        stringSerializator.write(fileName, studentKey.getName());
    }

    private IntegerSerializator integerSerializator = new IntegerSerializator();
    private StringSerializator stringSerializator = new StringSerializator();
}
