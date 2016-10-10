package ru.mipt.java2016.homework.g595.murzin.task1;

/**
 * Created by Дмитрий Мурзин on 10.10.16.
 */
public class TokenNumber extends Token {
	public final double x;

	public TokenNumber(String number) {
		super(TokenType.NUMBER);
		x = Double.valueOf(number);
	}

	public TokenNumber(double x) {
		super(TokenType.NUMBER);
		this.x = x;
	}

	@Override
	public String toString() {
		return String.valueOf(x);
	}
}
