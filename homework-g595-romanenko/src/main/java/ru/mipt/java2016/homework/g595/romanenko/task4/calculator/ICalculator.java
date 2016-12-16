package ru.mipt.java2016.homework.g595.romanenko.task4.calculator;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.List;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task4
 *
 * @author Ilya I. Romanenko
 * @since 26.11.16
 **/
public interface ICalculator {

    /**
     * Получить выражение, обозначенное переменной с указанным именем.
     */
    Double getVariable(String variableName);

    /**
     * Присвоить переменной новое выражение.
     */
    boolean putVariable(String variableName, Double value);

    /**
     * Удалить переменную с заданным именем.
     */
    boolean deleteVariable(String variableName);

    /**
     * Получить список имен всех переменных в сервисе.
     */
    List<String> getVariables();

    /**
     * Получить выражение, обозначенное функцией с указанным именем.
     * Также возвращает список аргументов функции.
     * Нельзя получить выражение для предопределенных функций.
     */
    CalculatorFunction getFunction(String functionName);

    /**
     * Присвоить функции с заданным именем следующее выражение и список аргументов.
     * Предопределенные функции нельзя изменить.
     */
    boolean putFunction(String functionName, List<String> args, String functionBody);

    /**
     * Удалить функцию с заданным именем. Предопределенные функции нельзя удалить.
     */
    boolean deleteFunction(String functionName);

    /**
     * Получить список имен всех пользовательских функций в сервисе.
     */
    List<String> getFunctionsNames();

    /**
     * Рассчитать значение выражения.
     */
    Double evaluate(String expression) throws ParsingException;
}
