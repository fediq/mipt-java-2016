package ru.mipt.java2016.homework.g595.murzin.task1;

import java.util.function.DoubleBinaryOperator;

/**
 * Created by Дмитрий Мурзин on 10.10.16.
 */
public class TokenOperatorBinary extends TokenOperator {
    public final DoubleBinaryOperator operator;

    public TokenOperatorBinary(TokenType type, int priority, DoubleBinaryOperator operator) {
        super(type, priority);
        this.operator = operator;
    }

    public TokenNumber apply(TokenNumber operand1, TokenNumber operand2) {
        return new TokenNumber(operator.applyAsDouble(operand1.x, operand2.x));
    }

    @Override
    public int numberOfOperands() {
        return 2;
    }
}
