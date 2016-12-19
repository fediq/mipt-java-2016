package ru.mipt.java2016.homework.g595.murzin.task1;

/**
 * Created by Дмитрий Мурзин on 10.10.16.
 */
public abstract class TokenOperator extends Token {
    public final int priority;

    public TokenOperator(TokenType type, int priority) {
        super(type);
        this.priority = priority;
    }

    public boolean isOperation() {
        return true;
    }

    public abstract int numberOfOperands();
}
