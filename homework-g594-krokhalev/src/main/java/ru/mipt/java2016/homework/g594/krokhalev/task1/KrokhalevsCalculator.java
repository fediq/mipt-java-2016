package ru.mipt.java2016.homework.g594.krokhalev.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

class KrokhalevsCalculator implements Calculator {

    private static final String INCORRECT_EXCEPTION = "Incorrect exception";

    private enum BlockType {
        OPERAND, ELEMENT
    }

    private class Block {
        private BlockType blockType;

        Block(BlockType blockType) {
            this.blockType = blockType;
        }
    }

    private class Operand extends Block {
        private double value;

        Operand(double value) {
            super(BlockType.OPERAND);
            this.value = value;
        }
    }

    private class Element extends Block {
        private final char figure;
        private final Integer order;
        private final Integer cntParams;

        Element(char figure, int cntParams) throws ParsingException {
            super(BlockType.ELEMENT);

            this.figure = figure;
            this.cntParams = cntParams;

            switch (figure) {
                case ')':
                case '(':
                    order = -1;
                    break;
                case '*':
                case '/':
                    order = 2;
                    break;
                case '-':
                case '+':
                    if (cntParams == 2) {
                        order = 3;
                    } else if (cntParams == 1) {
                        order = 1;
                    } else {
                        throw new ParsingException("Incorrect expression");
                    }
                    break;
                default:
                    throw new ParsingException("Incorrect expression");
            }
        }

        char getFigure() {
            return figure;
        }

        int getOrder() {
            return order;
        }

        Operand result(ArrayList<Operand> params) {
            switch (figure) {
                case '*':
                    return new Operand(params.get(0).value * params.get(1).value);
                case '/':
                    return new Operand(params.get(0).value / params.get(1).value);
                case '-':
                    if (cntParams == 2) {
                        return new Operand(params.get(0).value - params.get(1).value);
                    } else if (cntParams == 1) {
                        return new Operand(-params.get(0).value);
                    }
                case '+':
                    if (cntParams == 2) {
                        return new Operand(params.get(0).value + params.get(1).value);
                    } else if (cntParams == 1) {
                        return new Operand(params.get(0).value);
                    }
                default:
                    return null;
            }

        }
    }

    private Integer mPos = 0;
    private String  mExpression;

    private boolean isOperand() {
        return ((mExpression .charAt(mPos) >= '0' && mExpression .charAt(mPos) <= '9')
                || mExpression .charAt(mPos) == '.');
    }

    private boolean isFunction() {
        return (mExpression .charAt(mPos) == '+' || mExpression .charAt(mPos) == '-'
                || mExpression .charAt(mPos) == '/' || mExpression .charAt(mPos) == '*');
    }

    private boolean isBrace() {
        return (mExpression .charAt(mPos) == '(' || mExpression .charAt(mPos) == ')');
    }

    private boolean isSpace() {

        return mExpression .charAt(mPos) == ' ' || mExpression .charAt(mPos) == '\n'
                || mExpression .charAt(mPos) == '\t';
    }

    private boolean isRegistred() {
        return isOperand() || isFunction() || isBrace() || isSpace();
    }

    private boolean isInExpression() {
        return mPos < mExpression .length();
    }

    private char readNextChar() {
        if (isInExpression()) {
            return mExpression .charAt(mPos++);
        }
        return '\0';
    }

    private void readSpace() throws ParsingException {
        while (isInExpression() && isSpace()) {
            mPos++;
        }
        if (isInExpression() && !isRegistred()) {
            throw new ParsingException(INCORRECT_EXCEPTION);
        }
    }

    private Operand readOperand() throws ParsingException {
        String ans = "";
        do {
            ans += readNextChar();
        }
        while (isInExpression() && isOperand());
        try {
            Double dAns = Double.valueOf(ans);
            return new Operand(dAns);
        } catch (NumberFormatException e) {
            throw new ParsingException(INCORRECT_EXCEPTION);
        }
    }

    private Element readElement(Block prev) throws ParsingException {
        if (prev.blockType == BlockType.ELEMENT && ((Element) prev).figure != ')'
                && (mExpression.charAt(mPos) == '-' || mExpression.charAt(mPos) == '+')) {
            return new Element(readNextChar(), 1);
        } else if ((mExpression.charAt(mPos) == '(' || mExpression.charAt(mPos) == ')')) {
            return new Element(readNextChar(), 0);
        } else {
            return new Element(readNextChar(), 2);
        }
    }

    private Block readNext(Block prev) throws ParsingException {
        readSpace();
        if (!isInExpression()) {
            return null;
        }

        if (isOperand()) {
            return readOperand();
        } else {
            return readElement(prev);
        }
    }

    private Stack<Operand> operands = new Stack<>();
    private Stack<Element> elements = new Stack<>();

    private void popFunc() throws ParsingException {
        Element element = elements.peek();
        if (operands.size() < element.cntParams) {
            throw new ParsingException(INCORRECT_EXCEPTION);
        }

        ArrayList<Operand> params = new ArrayList<>();
        for (int i = 0; i < element.cntParams; ++i) {
            params.add(operands.peek());
            operands.pop();
        }
        Collections.reverse(params);

        operands.add(elements.peek().result(params));
        elements.pop();
    }

    private boolean canPop(Element element) {
        if (elements.size() == 0) {
            return false;
        }

        int eOrder = element.getOrder();
        int pOrder = elements.peek().getOrder();
        return (eOrder > 0 && pOrder > 0 && eOrder >= pOrder);
    }

    private void pushFunc(Element element) throws ParsingException {
        while (canPop(element)) {
            popFunc();
        }
        elements.add(element);
    }

    private void pushBracked() throws ParsingException {
        while (elements.size() > 0 && elements.peek().getFigure() != '(') {
            popFunc();
        }
        if (elements.size() == 0) {
            throw new ParsingException(INCORRECT_EXCEPTION);
        }
        elements.pop();
    }

    @Override
    public double calculate(String expression) throws ParsingException {
        mExpression  = '(' + expression + ')';

        Block curr;
        Block prev = new Element('(', 0);

        curr = readNext(prev);
        while (curr != null) {

            if (curr.blockType == BlockType.OPERAND) {
                operands.add((Operand) curr);
            } else {
                Element currElement = (Element) curr;

                if (currElement.figure == ')') {
                    pushBracked();
                } else {
                    pushFunc(currElement);
                }
            }

            prev = curr;
            curr = readNext(prev);
        }
        if (operands.size() == 0 || operands.size() > 1 || elements.size() > 0) {
            throw new ParsingException(INCORRECT_EXCEPTION);
        }

        return operands.get(0).value;
    }
}
