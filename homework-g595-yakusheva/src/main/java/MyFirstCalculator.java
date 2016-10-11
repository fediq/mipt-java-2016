import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

/**
 * Created by Софья on 04.10.2016.
 */
public class MyFirstCalculator implements ru.mipt.java2016.homework.base.task1.Calculator {

    private class Element {
        public int type = 0;
        public double value = 0;
        public char symbol = ' ';
        public boolean isUnary = false;

        Element(int t, double v, char c) {
            type = t;
            value = v;
            symbol = c;
        }
    }

    private String equation;
    private int iter_begin = 0;
    private boolean prevMinus = false;
    private boolean prevOper = false;
    private boolean prevSkob = true;
    private Stack<Element> eQ;
    private Stack<Element> iQ;

    private double getResult(double a, double b, char operand) throws ParsingException {
        double result;
        switch (operand) {
            case '+':
                result = a + b;
                break;
            case '-':
                result = a - b;
                break;
            case '*':
                result = a * b;
                break;
            case '/':
                result = a / b;
                break;
            default:
                throw new ParsingException("unknown operation");
        }
        return result;
    }

    private boolean isOperator(char symbol) {
        boolean answer;
        switch (symbol) {
            case '+':
            case '-':
            case '*':
            case '/':
            case '(':
            case ')':
                answer = true;
                break;
            default:
                answer = false;
        }
        return answer;
    }

    private boolean bracketBalance() {
        boolean ans = false;
        int balance = 0;
        for (int i = 0; i < equation.length(); i++) {
            if (equation.charAt(i) == '(') {
                balance++;
            } else {
                if (equation.charAt(i) == ')') {
                    balance--;
                }
            }
            if (balance < 0) {
                ans = true;
            }
        }
        if (balance > 0)
            ans = true;
        return ans;
    }

    private boolean doubleBracketError() {
        boolean ans = false;
        for (int i = 0; i < equation.length() - 1; i++) {
            if ((equation.charAt(i) == '(') && (equation.charAt(i) == ')')) {
                ans = true;
            }
            if ((equation.charAt(i) == ')') && (equation.charAt(i) == '(')) {
                ans = true;
            }
        }
        return ans;
    }

    private Element getNextStackElement() throws ParsingException {
        int newType = 0;
        double newValue = 0;
        char newSymbol = ' ';
        int i = iter_begin;
        if (i < equation.length()) {
            while ((i < equation.length()) && Character.isWhitespace(equation.charAt(i))) {
                i++;
            }
        }
        if (i < equation.length()) {
            if (isOperator(equation.charAt(i))) {
                newSymbol = equation.charAt(i);
                newType = 1;
                i++;
            } else {
                if (Character.isDigit(equation.charAt(i))) {
                    StringBuilder subString = new StringBuilder();
                    while ((i < equation.length()) && (Character.isDigit(equation.charAt(i)) || (equation.charAt(i) == '.'))) {
                        subString.append(equation.charAt(i));
                        i++;
                    }
                    try {
                        newValue = Double.parseDouble(subString.toString());
                    } catch (NumberFormatException e) {
                        throw new ParsingException("invalid numeric");
                    }
                    newType = 2;
                } else {
                    throw new ParsingException("unknown symbol");
                }
            }
        }
        iter_begin = i;
        return new Element(newType, newValue, newSymbol);
    }

    private int getOperationPriority(char symbol) {
        int pr;
        switch (symbol) {
            case '+':
            case '-':
                pr = 1;
                break;
            case '*':
            case '/':
                pr = 2;
                break;
            case '(':
            case ')':
                pr = 0;
                break;
            default:
                pr = -1;
        }
        return pr;
    }

    MyFirstCalculator() {
        eQ = new Stack<Element>();
        iQ = new Stack<Element>();
    }

    private void pushNextElement() throws ParsingException {
        Element nextElement = getNextStackElement();
        if (nextElement.type == 2) {
            eQ.push(nextElement);
            prevOper = false;
            prevSkob = false;
        } else {
            if (nextElement.type == 1) {
                if (prevOper && (nextElement.symbol == '-')) {
                    nextElement.isUnary = true;
                    iQ.push(nextElement);
                }
                else {
                    if ((prevOper) && (!prevSkob) && (nextElement.symbol == ')'))
                        throw new ParsingException("incorrect operations order");
                    if ((prevOper) && (!prevSkob) && (nextElement.symbol != '('))
                        throw new ParsingException("incorrect operations order");

                    if (getOperationPriority(nextElement.symbol) > 0) {
                        while ((!iQ.empty()) && (getOperationPriority(iQ.elementAt(iQ.size() - 1).symbol) >= (getOperationPriority(nextElement.symbol)))) {
                            Element newElement = iQ.lastElement();
                            iQ.pop();
                            eQ.push(newElement);
                        }
                        iQ.push(nextElement);
                    } else {
                        prevSkob = true;
                        if (nextElement.symbol != ')') {
                            iQ.push(nextElement);
                        } else {
                            while ((!iQ.empty()) && (getOperationPriority(iQ.elementAt(iQ.size() - 1).symbol) > (getOperationPriority(nextElement.symbol)))) {
                                Element newElement = iQ.lastElement();
                                iQ.pop();
                                eQ.push(newElement);
                            }
                            if (!iQ.empty())
                                iQ.pop();
                        }
                    }
                }
                prevOper = true;
            }
            else {
                if (nextElement.type == 0) {
                    while (!iQ.empty()) {
                        Element newElement = iQ.lastElement();
                        if (newElement.symbol != '(') {
                            iQ.pop();
                            eQ.push(newElement);

                        } else {
                            iQ.pop();
                        }
                    }
                }
            }
        }
    }


    public double calculate(String expression) throws ParsingException {
        equation = expression;
        prevOper = true;
        if (equation == null)
            throw new ParsingException("null string");
        if (equation.length() == 0)
            throw new ParsingException("empty expression");

        if (bracketBalance())
            throw new ParsingException(" find ( and ) disbalance");

        if (doubleBracketError())
            throw new ParsingException(" find empty () or )( ");

        while (iter_begin < equation.length())
            pushNextElement();

        pushNextElement();

        if (eQ.size() == 0)
            throw new ParsingException("empty expression");

        Stack<Double> myAnswer = new Stack<Double>();
        int y = 0;
        while (eQ.size() != y) {
            Element A = eQ.elementAt(y);
            y++;
            if (A.type == 2) {
                myAnswer.push(A.value);
            } else {
                if (myAnswer.size() == 0) {
                    throw new ParsingException("incorrect operation order");
                }
                else {
                    if(myAnswer.size() == 1){
                        if(A.symbol != '-'){
                            throw new ParsingException("incorrect operation order");
                        }
                    }
                    if(A.isUnary){
                        double tmp = myAnswer.lastElement();
                        myAnswer.pop();
                        tmp = -tmp;
                        myAnswer.push(tmp);
                    }
                    else{
                        double first = myAnswer.lastElement();
                        myAnswer.pop();
                        double second = myAnswer.lastElement();
                        myAnswer.pop();
                        myAnswer.push(getResult(second, first, A.symbol));
                    }

                }
            }
        }
        return myAnswer.firstElement();
    }
}
