package ru.mipt.java2016.homework.base.task1;

/**
 * Не удалось распознать выражение
 *
 * @author Fedor S. Lavrentyev
 * @since 28.09.16
 */
public class ParsingException extends Exception {
    public ParsingException(String message) {
        super(message);
    }

    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
