package ru.mipt.java2016.homework.g499.shchelchkov.task1;

/**
 * Created by demikandr on 10/17/16.
 */
public class Token {
    public enum TokenType {
        L_BRACE, R_BRACE, NUM, PLUS, MINUS, MULT, DIV, END, BEGIN;
    }

    private TokenType tokenType;
    private Double num;

    public Double getNum() {
        return this.num;
    }

    public TokenType getTokenType() {
        return this.tokenType;
    }

    public Token(TokenType tokenType) {
        assert tokenType != TokenType.NUM;
        this.tokenType = tokenType;
        this.num = null;
    }

    public Token(TokenType tokenType, Double num) {
        assert tokenType == TokenType.NUM;
        this.tokenType = tokenType;
        this.num = num;
    }
}