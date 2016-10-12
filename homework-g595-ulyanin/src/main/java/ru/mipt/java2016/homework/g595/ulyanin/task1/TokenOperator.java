package ru.mipt.java2016.homework.g595.ulyanin.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by ulyanin on 12.10.16.
 */

public class TokenOperator extends Token {

    public enum Associativity { LEFT, RIGHT }

    private boolean unary = false;

    public TokenOperator(String operatorSymbol) {
        super(operatorSymbol, TokenType.OPERATOR);
    }

    public TokenOperator(String operatorSymbol, TokenType tokenType) {
        super(operatorSymbol, tokenType);
    }

    public TokenOperator(String operatorSymbol, boolean isUnary) {
        super(operatorSymbol, TokenType.OPERATOR);
        unary = isUnary;
    }

    public int getPrecedence() throws ParsingException {
        final String ops = "()+-*/";
        int index = ops.indexOf(data);
        if (index == -1) {
            throw new ParsingException("unknown operator " + data);
        }
        if (isSubtruct() && isUnary()) {
            return 3;
        }
        return index / 2;
    }

    public boolean isSubtruct() {
        return data.equals("-");
    }

    public boolean isAddition() {
        return data.equals("+");
    }

    public boolean isMultiply() {
        return data.equals("*");
    }

    public boolean isDivision() {
        return data.equals("/");
    }

    public double apply(double var1, double var2) throws ParsingException {
        if (isUnary()) {
            throw new ParsingException("trying to apply " + data + " as binary operator");
        }
        if (isAddition()) {
            return var1 + var2;
        }
        if (isMultiply()) {
            return var1 * var2;
        }
        if (isSubtruct()) {
            return var1 - var2;
        }
        if (isDivision()) {
            return var1 / var2;
        }
        return 0;
    }

    public double apply(double var) throws ParsingException {
        if (!isUnary()) {
            throw new ParsingException("trying to apply " + data + " as unary");
        }
        if (isAddition()) {
            throw new ParsingException("+ is not unary operator");
        }
        if (isMultiply()) {
            throw new ParsingException("* is not unary operator");
        }
        if (isSubtruct()) {
            return (-1) * var;
        }
        if (isDivision()) {
            throw new ParsingException("/ is not unary operator");
        }
        return 0;
    }

    public boolean isUnary() {
        return unary;
    }

    public boolean isRightAssociativity() {
        return isUnary();
    }

    public boolean isLeftAssociativity() {
        return !isRightAssociativity();
    }
}
