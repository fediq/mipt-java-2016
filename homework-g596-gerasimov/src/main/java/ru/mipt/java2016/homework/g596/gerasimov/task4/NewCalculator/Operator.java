package ru.mipt.java2016.homework.g596.gerasimov.task4.NewCalculator;

import java.util.Stack;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by geras-artem on 19.12.16.
 */
public enum Operator {

    PLUS(1, 2, true), MINUS(1, 2, true), MULTIPLY(2, 2, true), DIVIDE(2, 2, true), U_PLUS(3, 1,
            false), U_MINUS(3, 1, false);

    private int priority;
    private int valency;
    private boolean isLA;

    Operator(int priority, int valency, boolean isLA) {
        this.priority = priority;
        this.valency = valency;
        this.isLA = isLA;
    }

    public void use(Stack<Double> nums) throws ParsingException {
        if (nums.size() < valency) {
            throw new ParsingException("Wrong expression");
        }

        double tmp;
        switch (this) {
            case PLUS:
                tmp = nums.pop();
                tmp += nums.pop();
                break;
            case MINUS:
                tmp = -nums.pop();
                tmp += nums.pop();
                break;
            case MULTIPLY:
                tmp = nums.pop();
                tmp *= nums.pop();
                break;
            case DIVIDE:
                tmp = 1 / nums.pop();
                tmp *= nums.pop();
                break;
            case U_PLUS:
                tmp = nums.pop();
                break;
            case U_MINUS:
                tmp = -nums.pop();
                break;
            default:
                throw new IllegalStateException();
        }
        nums.push(tmp);
    }

    public int getPriority() {
        return priority;
    }

    public int getValency() {
        return valency;
    }

    public boolean getIsLA() {
        return isLA;
    }
}
