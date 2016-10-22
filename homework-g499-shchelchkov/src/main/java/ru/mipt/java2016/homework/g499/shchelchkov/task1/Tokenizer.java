package ru.mipt.java2016.homework.g499.shchelchkov.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by demikandr on 10/17/16.
 */


public class Tokenizer {
    private String expression;
    private Token currentToken = new Token(Token.TokenType.BEGIN);
    private int index;

    public Tokenizer(String expression) throws ParsingException {
        this.expression = expression.replaceAll("\\s+", "");
        this.index = 0;
        nextToken();
        
    }

    private void readNumToken() throws ParsingException {
        String numString = "";
        if (!Character.isDigit(this.expression.charAt(this.index))) {
            throw new ParsingException("Number was expected");
        }
        do {
            numString += this.expression.charAt(index);
            ++this.index;
        }
        while ((this.index < this.expression.length()) && (Character.isDigit(this.expression.charAt(this.index))));
        if ((this.index < this.expression.length()) && (".,".indexOf(this.expression.charAt(this.index)) != -1)) {
            numString += this.expression.charAt(index);
            ++this.index;
        }
        while ((this.index < this.expression.length()) && (Character.isDigit(this.expression.charAt(this.index)))) {
            numString += this.expression.charAt(this.index);
            ++this.index;
        }
        --this.index;
        this.currentToken = new Token(Token.TokenType.NUM, Double.parseDouble(numString));
    }

    public void nextToken() throws ParsingException {
        if (this.index == this.expression.length()) {
            throw new ParsingException("Unexpected end of expression");
        }
        switch (this.expression.charAt(this.index)) {
            case '(':
                this.currentToken = new Token(Token.TokenType.L_BRACE);
                break;
            case ')':
                this.currentToken = new Token(Token.TokenType.R_BRACE);
                break;
            case '+':
                this.currentToken = new Token(Token.TokenType.PLUS);
                break;
            case '-':
                this.currentToken = new Token(Token.TokenType.MINUS);
                break;
            case '*':
                this.currentToken = new Token(Token.TokenType.MULT);
                break;
            case '/':
                this.currentToken = new Token(Token.TokenType.DIV);
                break;
            case 'n':
                this.currentToken = new Token(Token.TokenType.END);
                break;
            default:
                this.readNumToken();
        }
        ++this.index;
    }

    public Token getCurrentToken() {
        return this.currentToken;
    }
}
