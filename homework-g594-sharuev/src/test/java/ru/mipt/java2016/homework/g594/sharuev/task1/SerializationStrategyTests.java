package ru.mipt.java2016.homework.g594.sharuev.task1;

import org.junit.Assert;
import org.junit.Test;
import ru.mipt.java2016.homework.g594.sharuev.task2.JsonSerializationStrategy;
import ru.mipt.java2016.homework.g594.sharuev.task2.ReflectSerializationStrategy;
import ru.mipt.java2016.homework.g594.sharuev.task2.SerializationException;
import ru.mipt.java2016.homework.tests.task2.Student;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SerializationStrategyTests {
    @Test
    public void testReflectiveReadWrite() throws SerializationException {
        ReflectSerializationStrategy<Student> strategy = new ReflectSerializationStrategy<>();
        Student toSerialize;
        try {
            toSerialize = new Student(594, "Nobody", "Notown", new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2000"), true, -0.1);
        } catch (ParseException e) {
            throw new SerializationException("Test init failed", e);
        }
        byte[] ans = strategy.serializeToBytes(toSerialize);
        Student fromSerialize = strategy.deserialize(ans);
        Assert.assertEquals(ans.toString(), null);
        Assert.assertEquals(toSerialize, fromSerialize);

    }

    @Test
    public void testJsonReadWrite() throws SerializationException {
        JsonSerializationStrategy<Student> strategy = new JsonSerializationStrategy<>();
        Student toSerialize;
        try {
            toSerialize = new Student(594, "Nobody", "Notown", new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2000"), true, -0.1);
        } catch (ParseException e) {
            throw new SerializationException("Test init failed", e);
        }
        byte[] ans = strategy.serializeToBytes(toSerialize);
        Student fromSerialize = strategy.deserialize(ans);
        Assert.assertEquals(ans.toString(), null);
        Assert.assertEquals(toSerialize, fromSerialize);

    }
}
