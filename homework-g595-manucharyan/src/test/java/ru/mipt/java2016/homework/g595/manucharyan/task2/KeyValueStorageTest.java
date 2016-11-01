package ru.mipt.java2016.homework.g595.manucharyan.task2;

import java.io.IOException;

import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * @author Vardan Manucharyan
 * @since 30.10.16
 */
public class KeyValueStorageTest extends AbstractSingleFileStorageTest {
    private ConcreteStrategyString cs1 = new ConcreteStrategyString(), cs2 = new ConcreteStrategyString();

    @Override
    protected KeyValueStorageImpl<String, String> buildStringsStorage(String Path) {
        try {
            return new KeyValueStorageImpl<>(cs1, cs2, Path);
        } catch (IOException e) {
            throw new RuntimeException("Op, you got a problems");
        }
    }

    private ConcreteStrategyInteger cs3 = new ConcreteStrategyInteger();
    private ConcreteStrategyDouble cs4 = new ConcreteStrategyDouble();

    @Override
    protected KeyValueStorageImpl<Integer, Double> buildNumbersStorage(String Path) {
        try {
            return new KeyValueStorageImpl<>(cs3, cs4, Path);
        } catch (IOException e) {
            throw new RuntimeException("Op, you got a problems");
        }
    }

    private ConcreteStrategyStudentKey cs5 = new ConcreteStrategyStudentKey();
    private ConcreteStrategyStudent cs6 = new ConcreteStrategyStudent();

    @Override
    protected KeyValueStorageImpl<StudentKey, Student> buildPojoStorage(String Path) {
        try {
            return new KeyValueStorageImpl<>(cs5, cs6, Path);
        } catch (IOException e) {
            throw new RuntimeException("Op, you got a problems");
        }
    }
}
