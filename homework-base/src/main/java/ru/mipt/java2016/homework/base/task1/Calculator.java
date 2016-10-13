package ru.mipt.java2016.homework.base.task1;

/**
 * @author Fedor S. Lavrentyev
 * @since 28.09.16
 */
public interface Calculator {
    /**
     * Принимает строку с валидным арифметическим выражением.
     * Возвращает результат выполнения этого выражения.
     * Выражение может содержать числа десятичного формата, операторы +, -, *, / и операторы приоритета (, ).
     * В выражении допустимы любые space-символы.
     *
     * @param expression строка с арифметическим выражением
     * @return           результат расчета выражения
     * @throws ParsingException
     *                   не удалось распознать выражение
     */
    double calculate(String expression) throws ParsingException;

    //double calculate(String expression) throws ParsingException;

    //double calculate(String expression) throws ParsingException;
}
