package ru.mipt.java2016.homework.g597.bogdanov.task4.CalculatorWithTokens;

/**
 * Created by Semyo_000 on 20.12.2016.
 */
public class Token {
    public enum TokenType {
        PLUS, MINUS, MULTIPLY, DIVIDE,
        NUMBER, NAME,
        LEFT_BRACE, RIGHT_BRACE, COMMA,
        UNKNOWN
    }

    private final TokenType type;
    private Double number = null;
    private String name = null;

    public Token(TokenType type) {
        this.type = type;
    }

    public Token(String name) {
        this.name = name;
        this.type = TokenType.NAME;
    }

    public Token(Double number) {
        this.number = number;
        this.type = TokenType.NUMBER;
    }

    public TokenType getType() {
        return type;
    }

    public Double getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("Type: %s, Number: %s, Name: %s \n", type, number, name);
    }
}