package ru.mipt.java2016.homework.g597.grishutin.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

public class Operator extends Token {

    public enum Associativity { LEFT,
                                RIGHT }

    private boolean unary = false;

    public Operator(String operatorSymbol) {
        super(operatorSymbol, TokenType.OPERATOR);
    }

    public Operator(String operatorSymbol, TokenType tokenType) {
        super(operatorSymbol, tokenType);
    }

    public Operator(String operatorSymbol, boolean isUnary) {
        super(operatorSymbol, TokenType.OPERATOR);
        unary = isUnary;
    }

    public int getPriority() {
        if (data.equals("(") || data.equals(")")) {
            return 0;
        } else if (data.equals("-") && isUnary()) {
            return 3;
        } else if (data.equals("+") || data.equals("-")) {
            return 1;
        } else if (data.equals("*") || data.equals("/")) {
            return 2;
        } else {
            return 4;
        }
    }

    public double apply(double var1, double var2) throws ParsingException {
        if (isUnary()) {
            throw new ParsingException(String.format("Arity of %s operator is wrong", data));
        }
        if (data.equals("+")) {
            return var1 + var2;
        }
        if (data.equals("-")) {
            return var1 - var2;
        }
        if (data.equals("*")) {
            return var1 * var2;
        }
        if (data.equals("/")) {
            return var1 / var2;
        }
        return 0;
    }

    public double apply(double var) throws ParsingException {
        if (!isUnary()) {
            throw new ParsingException(String.format("Arity of %s operator is wrong", data));
        }
        if (isAddition()) {
            throw new ParsingException("+ is not unary operator");
        }
        if (isMultiplying()) {
            throw new ParsingException("* is not unary operator");
        }
        if (isSubstruction()) {
            return (-1) * var;
        }
        if (isDividing()) {
            throw new ParsingException("/ is not unary operator");
        }
        return 0;
    }

    public boolean isUnary() {
        return unary;
    }

    public boolean isRightAssociative() {
        return isUnary();
    }

    public boolean isLeftAssociative() {
        return !isRightAssociative();
    }
    public boolean isSubstruction() {
        return data.equals("-");
    }

    public boolean isAddition() {
        return data.equals("+");
    }

    public boolean isMultiplying() {
        return data.equals("*");
    }

    public boolean isDividing() {
        return data.equals("/");
    }
}