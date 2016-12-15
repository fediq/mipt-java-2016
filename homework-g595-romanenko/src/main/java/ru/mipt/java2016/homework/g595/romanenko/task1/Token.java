package ru.mipt.java2016.homework.g595.romanenko.task1;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task1
 *
 * @author Ilya I. Romanenko
 * @since 12.10.2016
 **/

class Token {

    enum ExpressionToken {
        MUL, MINUS, NUMBER, LEFT_BRACES, RIGHT_BRACES, PLUS, DIVIDE
    }

    private final ExpressionToken type;
    private Double number = null;

    Token(ExpressionToken type) {
        this.type = type;
    }

    Token(Double number) {
        this.number = number;
        this.type = ExpressionToken.NUMBER;
    }

    ExpressionToken getType() {
        return type;
    }

    Double getNumber() {
        return number;
    }
}
