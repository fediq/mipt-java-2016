package ru.mipt.java2016.homework.g499.shchelchkov.task1;


import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by demikandr on 10/17/16.
 */
public class DCalculator implements Calculator {
    private Tokenizer tokenizer;

    private double calculateMultiplyer() throws ParsingException {
        double multiplyer;
        switch (this.tokenizer.getCurrentToken().getTokenType()) {
            case NUM:
                multiplyer = this.tokenizer.getCurrentToken().getNum();
                this.tokenizer.nextToken();
                break;
            case L_BRACE:
                multiplyer = this.calculateSum();
                break;
            case MINUS:
                this.tokenizer.nextToken();
                multiplyer = -this.calculateMultiplyer();
                break;
            default:
                throw new ParsingException("Invalid token after '*'|'/'");
        }
        return multiplyer;
    }

    private double calculateProduct() throws ParsingException {
        double product = calculateMultiplyer();

        while   ((this.tokenizer.getCurrentToken().getTokenType() == Token.TokenType.MULT) ||
                (this.tokenizer.getCurrentToken().getTokenType() == Token.TokenType.DIV)) {
            Token.TokenType action = this.tokenizer.getCurrentToken().getTokenType();
            this.tokenizer.nextToken();
            double term = calculateMultiplyer();
            switch (action) {
                case MULT:
                    product *= term;
                    break;
                case DIV:
                    product /= term;
                    break;
                default:
                    assert false;
            }
        }
        return product;
    }

    private double calculateSum() throws ParsingException {
        assert this.tokenizer.getCurrentToken().getTokenType() == Token.TokenType.L_BRACE;
        double sum = 0;
        this.tokenizer.nextToken();
        do  {
            double coef = 1;
            if ((this.tokenizer.getCurrentToken().getTokenType() == Token.TokenType.MINUS) ||
                    (this.tokenizer.getCurrentToken().getTokenType() == Token.TokenType.PLUS)) {
                if (this.tokenizer.getCurrentToken().getTokenType() == Token.TokenType.MINUS) {
                    coef *= -1;
                }
                this.tokenizer.nextToken();
            }
            switch (this.tokenizer.getCurrentToken().getTokenType()) {
                case NUM:
                case L_BRACE:
                    sum += coef * this.calculateProduct();
                    break;
                default:
                    throw new ParsingException("Invalid token after '+'");
            }
        } while ((this.tokenizer.getCurrentToken().getTokenType() == Token.TokenType.PLUS) ||
                 (this.tokenizer.getCurrentToken().getTokenType() == Token.TokenType.MINUS));
        if (this.tokenizer.getCurrentToken().getTokenType() != Token.TokenType.R_BRACE) {
            throw new ParsingException("')' was expected");
        }
        this.tokenizer.nextToken();
        return sum;
    }

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("No expression was passed");
        }
        this.tokenizer = new Tokenizer("(" + expression + ")n");
        double result = calculateSum();
        if (this.tokenizer.getCurrentToken().getTokenType() != Token.TokenType.END) {
            throw new ParsingException("Unexpected ')'");
        }
        return result;
    }
}