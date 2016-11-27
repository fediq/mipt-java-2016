package ru.mipt.java2016.homework.g595.romanenko.task1;


/**
 * ru.mipt.java2016.homework.g595.romanenko.task1
 *
 * @author Ilya I. Romanenko
 * @since 12.10.2016
 **/

public class Token {

    public enum TokenType {
        MUL, MINUS, NUMBER, LEFT_BRACES, RIGHT_BRACES, PLUS, DIVIDE, NAME, UNKNOWN, COMMA
    }

    private final TokenType type;
    private Double number = null;
    private String name = null;

    public Token(TokenType type) {
        this.type = type;
    }

    public Token(String str) {
        this.name = str;
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
        return String.format("Type : %s, Number : %s, Name : %s", type, number, name);
    }
}
