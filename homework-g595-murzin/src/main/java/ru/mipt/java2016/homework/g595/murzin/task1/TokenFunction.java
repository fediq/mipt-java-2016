package ru.mipt.java2016.homework.g595.murzin.task1;

/**
 * Created by Дмитрий Мурзин on 10.10.16.
 */
public class TokenFunction extends Token {
    public final MyFunction function;

    public TokenFunction(MyFunction function) {
        super(TokenType.FUNCTION);
        this.function = function;
    }
}
