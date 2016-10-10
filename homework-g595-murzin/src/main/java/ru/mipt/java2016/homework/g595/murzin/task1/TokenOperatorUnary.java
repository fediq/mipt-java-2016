package ru.mipt.java2016.homework.g595.murzin.task1;

import java.util.function.DoubleUnaryOperator;

/**
 * Created by Дмитрий Мурзин on 10.10.16.
 */
public class TokenOperatorUnary extends TokenOperator {
	public final DoubleUnaryOperator operator;

	public TokenOperatorUnary(TokenType type, int priority, DoubleUnaryOperator operator) {
		super(type, priority);
		this.operator = operator;
	}

	public TokenNumber apply(TokenNumber operand) {
		return new TokenNumber(operator.applyAsDouble(operand.x));
	}

	@Override
	public int numberOfOperands() {
		return 1;
	}
}
