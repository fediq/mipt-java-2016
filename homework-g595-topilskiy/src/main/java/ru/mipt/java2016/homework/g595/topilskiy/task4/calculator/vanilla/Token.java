package ru.mipt.java2016.homework.g595.topilskiy.task4.calculator.vanilla;

/**
 * Token class for parsing the expression in TokenCalculator
 *
 * @author Artem K. Topilskiy
 * @since  16.12.16.
 */
public class Token {
    /**
     *  Enum to describe the type of Data in Token
     */
    public enum TokenType {
        PLUS, MINUS, MULTIPLY, DIVIDE,
        NUMBER, NAME,
        LEFT_BRACE, RIGHT_BRACE, COMMA,
        UNKNOWN
    }

    /**
     *  Data
     */
    private final TokenType type;
    private Double number = null;
    private String name   = null;

    /**
     *  Constructors
     */
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


    /**
     * Getters
     */
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
