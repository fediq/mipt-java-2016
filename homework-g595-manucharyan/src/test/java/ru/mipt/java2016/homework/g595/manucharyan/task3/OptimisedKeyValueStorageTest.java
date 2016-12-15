package ru.mipt.java2016.homework.g595.manucharyan.task3;

import java.io.IOException;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * @author Vardan Manucharyan
 * @since 20.11.16
 */
public class OptimisedKeyValueStorageTest extends KeyValueStoragePerformanceTest {
    private ConcreteStrategyStringRandomAccess
            cs1 = new ConcreteStrategyStringRandomAccess(),
            cs2 = new ConcreteStrategyStringRandomAccess();

    @Override
    protected OptimisedKeyValueStorage<String, String> buildStringsStorage(String Path) {
        try {
            return new OptimisedKeyValueStorage<>(cs1, cs2, Path);
        } catch (IOException e) {
            throw new RuntimeException("Op, you got a problems");
        }
    }

    private ConcreteStrategyIntegerRandomAccess
            cs3 = new ConcreteStrategyIntegerRandomAccess();
    private ConcreteStrategyDoubleRandomAccess
            cs4 = new ConcreteStrategyDoubleRandomAccess();

    @Override
    protected OptimisedKeyValueStorage<Integer, Double> buildNumbersStorage(String Path) {
        try {
            return new OptimisedKeyValueStorage<>(cs3, cs4, Path);
        } catch (IOException e) {
            throw new RuntimeException("Op, you got a problems");
        }
    }

    private ConcreteStrategyStudentKeyRandomAccess
            cs5 = new ConcreteStrategyStudentKeyRandomAccess();
    private ConcreteStrategyStudentRandomAccess
            cs6 = new ConcreteStrategyStudentRandomAccess();

    @Override
    protected OptimisedKeyValueStorage<StudentKey, Student> buildPojoStorage(String Path) {
        try {
            return new OptimisedKeyValueStorage<>(cs5, cs6, Path);
        } catch (IOException e) {
            throw new RuntimeException("Op, you got a problems");
        }
    }
}
