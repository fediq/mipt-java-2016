package ru.mipt.java2016.homework.g597.vasilyev.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

/**
 * Created by mizabrik on 21.12.16.
 */
public class UserCommand implements Command {
    private ExtendableCalculator calculator;
    private String expression;
    private String[] args;
    private Scope oldScope;

    public UserCommand(String expression, String[] args, ExtendableCalculator calculator, Scope scope) {
        this.expression = expression;
        this.args = args;
        this.calculator = calculator;
        this.oldScope = scope;
    }

    @Override
    public void apply(Stack<Double> stack) throws ParsingException {
        OverridingScope scope = new OverridingScope(oldScope);

        for (int i = args.length - 1; i >= 0; --i) {
            scope.addOverride(args[i], new PushNumberCommand(stack.pop()));
        }

        stack.push(calculator.calculate(expression, scope));
    }
}
