package ru.mipt.java2016.homework.g397.kulikov.task1;

/**
 * @author aq
 * @since 05.12.16.
 */

public class QToken {

    enum TokType {
        NUMBER,
        ADD, SUB, MUL, DIV,
        LPAREN, RPAREN,
        END
    }

    private final TokType type;
    private final Double value;

    QToken(TokType type) {
        this.type = type;
        this.value = null;
    }

    QToken(Double number) {
        this.value = number;
        this.type = TokType.NUMBER;
    }

    TokType getType() {
        return type;
    }

    Double getNumber() {
        return value;
    }
}
