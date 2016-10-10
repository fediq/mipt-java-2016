package ru.mipt.java2016.homework.g595.murzin.task1;

/**
 * Created by Дмитрий Мурзин on 10.10.16.
 */
public class Token {
	public static final Token MINUS_UNARY = new TokenOperatorUnary(TokenType.MINUS_UNARY, 3, a -> -a);
	public static final Token PLUS = new TokenOperatorBinary(TokenType.PLUS, 1, (a, b) -> a + b);
	public static final Token MINUS = new TokenOperatorBinary(TokenType.MINUS, 1, (a, b) -> a - b);
	public static final Token MULTIPLY = new TokenOperatorBinary(TokenType.MULTIPLY, 2, (a, b) -> a * b);
	public static final Token DIVIDE = new TokenOperatorBinary(TokenType.DIVIDE, 2, (a, b) -> a / b);
	public static final Token OPEN_BRACKET = new Token(TokenType.OPEN_BRACKET);
	public static final Token CLOSE_BRACKET = new Token(TokenType.CLOSE_BRACKET);

	public final TokenType type;

	public Token(TokenType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type.name();
	}

	public boolean isOperation() {
		return false;
	}
}
