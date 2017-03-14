package ru.mipt.java2016.homework.g596.narsia.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.*;


public class MyCalculator implements Calculator {

    private String whatIsIt(Character symbol) {
        switch (symbol) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            {
                return "Digit";
            }
            case '.': {
                return "Point";
            }
            case '+':
            case '*':
            case '/':
            {
                return "Usual operator";
            }
            case '-': {
                return "Minus";
            }
            case '(': {
                return "Opening bracket";
            }
            case ')': {
                return "Closing bracket";
            }
            case ' ':
            case '\t':
            case '\n':
            {
                return "Space";
            }
            case '~': {
                return "First";
            }
            default: {
                return "So bad";
            }
        }
    }


    private Integer getCode(Character first, Character second) {
        switch (first) {
            case '~': {
                switch (second) {
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case '(':
                    {
                        return 1;
                    }
                    case '~': {
                        return 4;
                    }
                    case ')': {
                        return 5;
                    }
                    default:
                    {
                        return -1;
                    }
                }
            }
            case '+':
            case '-': {
                switch (second) {
                    case '*':
                    case '/':
                    case '(':
                    {
                        return 1;
                    }
                    case '~':
                    case ')':
                    case '+':
                    case '-':
                    {
                        return 2;
                    }
                    default: {
                        return -1;
                    }
                }
            }
            case '*':
            case '/': {
                switch (second) {
                    case '(': {
                        return 1;
                    }
                    case '~':
                    case ')':
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    {
                        return 2;
                    }
                    default: {
                        return -1;
                    }
                }
            }
            case '(': {
                switch (second) {
                    case '(':
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    {
                        return 1;
                    }
                    case ')': {
                        return 3;
                    }
                    case '~': {
                        return 5;
                    }
                    default: {
                        return -1;
                    }
                }
            }
            default: {
                return -1;
            }
        }
    }


    private void isAlmostValid(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Invalid expression");
        }
        Character prevSymbol = '~';
        Character curSymbol;
        Character importantPrevSymbol = '~';
        Boolean pointFlag = false;
        Boolean spaceFlag = false;

        for (int cnt = 0; cnt < expression.length(); ++cnt) {
            curSymbol = expression.charAt(cnt);
            switch (whatIsIt(curSymbol)) {
                case "Digit": {
                    switch (whatIsIt(prevSymbol)) {
                        case "Space": {
                            switch (whatIsIt(importantPrevSymbol)) {
                                case "Digit":
                                case "Closing bracket":
                                {
                                    throw new ParsingException("Invalid expression");
                                }
                            }
                            break;
                        }
                        case "Closing bracket": {
                            throw new ParsingException("Invalid expression");
                        }
                    }
                    break;
                }

                case "Point": {
                    switch (whatIsIt(prevSymbol)) {
                        case "Space":
                        case "Opening bracket":
                        case "Closing bracket":
                        case "Usual operator":
                        case "Minus":
                        case "First":
                        case "Point":
                        {
                            throw new ParsingException("Invalid expression");
                        }
                        case "Digit": {
                            if (!pointFlag) {
                                pointFlag = true;
                            } else {
                                throw new ParsingException("2 points in one number");
                            }
                            break;
                        }
                    }
                    break;
                }

                case "Usual operator": {
                    switch (whatIsIt(importantPrevSymbol)) {
                        case "Usual operator":
                        case "Opening bracket":
                        case "First":
                        case "Point":
                        {
                            throw new ParsingException("Invalid expression");
                        }
                        case "Digit": {
                            pointFlag = false;
                            break;
                        }
                    }
                    break;
                }

                case "Minus": {
                    switch (whatIsIt(importantPrevSymbol)) {
                        case "Point": {
                            throw new ParsingException("Invalid expression");
                        }
                        case "Digit": {
                            pointFlag = false;
                            break;
                        }
                    }
                    break;
                }

                case "Space": {
                    switch (whatIsIt(prevSymbol)) {
                        case "Point": {
                            throw new ParsingException("Invalid expression");
                        }
                        case "Digit": {
                            pointFlag = false;
                            break;
                        }
                    }
                    break;
                }

                case "Opening bracket": {
                    switch (whatIsIt(importantPrevSymbol)) {
                        case "Point":
                        case "Digit":
                        case "Closing bracket":
                        {
                            throw new ParsingException("Invalid expression");
                        }
                    }
                    break;
                }

                case "Closing bracket": {
                    switch (whatIsIt(importantPrevSymbol)) {
                        case "Point":
                        case "Opening bracket":
                        case "First":
                        case "Usual operator":
                        case "Minus":
                        {
                            throw new ParsingException("Invalid expression");
                        }
                        case "Digit": {
                            pointFlag = false;
                            break;
                        }
                    }
                    break;
                }

                case "So bad":
                case "First":
                {
                    throw new ParsingException("Invalid expression");
                }
            }
            prevSymbol = curSymbol;
            if (!whatIsIt(curSymbol).equals("Space")) {
                importantPrevSymbol = curSymbol;
                spaceFlag = true;
            }
        }
        if (!spaceFlag) {
            throw new ParsingException("Invalid expression");
        }
    }


    private StringBuilder removeSpaces(String expression) {
        StringBuilder result = new StringBuilder(expression.length());
        for (int cnt = 0; cnt < expression.length(); ++cnt) {
            if (!whatIsIt(expression.charAt(cnt)).equals("Space")) {
                result.append(expression.charAt(cnt));
            }
        }
        return result;
    }


    private StringBuilder removeUnaryMinuses(StringBuilder expressionWithoutSpaces) {
        expressionWithoutSpaces.append('~');
        StringBuilder result = new StringBuilder(expressionWithoutSpaces.length());
        Boolean flag = false;
        for (int cnt = 0; cnt < expressionWithoutSpaces.length(); ++cnt) {
            if (expressionWithoutSpaces.charAt(cnt) == '-') {
                if (cnt == 0) {
                    result.append('0');
                } else {
                    switch (whatIsIt(expressionWithoutSpaces.charAt(cnt - 1))) {
                        case "Opening bracket": {
                            result.append("0-");
                            continue;
                        }
                        case "Usual operator":
                        case "Minus": {
                            result.append("(0-");
                            flag = true;
                            continue;
                        }
                    }
                }
            }
            if ((!whatIsIt(expressionWithoutSpaces.charAt(cnt)).equals("Digit")) &&
                    (!whatIsIt(expressionWithoutSpaces.charAt(cnt)).equals("Point")) &&
                    (cnt > 0) && (flag)) {
                result.append(")");
                flag = false;
            }
            if (expressionWithoutSpaces.charAt(cnt) != '~') {
                result.append(expressionWithoutSpaces.charAt(cnt));
            }
        }
        return result;
    }


    private StringBuilder getRPN(StringBuilder expression) throws ParsingException {
        expression.append('~');
        Character curSymbol;
        Character prevSymbol = '~';
        Stack<Character> Texas = new Stack<>();
        StringBuilder California = new StringBuilder(expression.length());
        Texas.push('~');

        Integer cnt = 0;
        while (true) {
            curSymbol = expression.charAt(cnt);
            if (cnt > 0) {
                prevSymbol = expression.charAt(cnt - 1);
            }
            switch (whatIsIt(curSymbol)) {
                case "Digit":
                case "Point": {
                    switch (whatIsIt(prevSymbol)) {
                        case "Digit":
                        case "Point":
                        case "First": {
                            California.append(curSymbol);
                            ++cnt;
                            break;
                        }
                        default: {
                            California.append(" ");
                            California.append(curSymbol);
                            ++cnt;
                            break;
                        }
                    }
                    continue;
                }
            }

            switch (getCode(Texas.peek(), curSymbol)) {
                case 1: {
                    Texas.push(curSymbol);
                    ++cnt;
                    break;
                }
                case 2: {
                    California.append(" ");
                    California.append(Texas.peek());
                    Texas.pop();
                    break;
                }
                case 3: {
                    Texas.pop();
                    ++cnt;
                    break;
                }
                case 4: {
                    return California;
                }
                case 5: {
                    throw new ParsingException("Invalid bracket balance");
                }
            }
        }
    }


    private Double doOperation(Double first, Double second, Character operator) {
        switch (operator) {
            case '+': {
                return first + second;
            }
            case '-': {
                //сейчас будет костыль
                if ((first == 0) && (second == 0)) {
                    return -0.0;
                }
                return first - second;
            }
            case '*': {
                return first * second;
            }
            case '/': {
                return first / second;
            }
        }
        return -1.0;
    }


    @Override
    public double calculate(String expression) throws ParsingException {
        isAlmostValid(expression);
        StringBuilder withoutSpaces = removeSpaces(expression);
        StringBuilder withoutSpacesAndUnaryMinuses = removeUnaryMinuses(withoutSpaces);
        StringBuilder rpn = getRPN(withoutSpacesAndUnaryMinuses);
        Stack<Double> Numbers = new Stack<>();
        Double first, second;
        StringBuilder curNumber = new StringBuilder();
        Character curChar = '~';
        Character prevChar;
        for (int cnt = 0; cnt < rpn.length(); ++cnt) {
            prevChar = curChar;
            curChar = rpn.charAt(cnt);
            switch (whatIsIt(curChar)) {
                case "Digit":
                case "Point": {
                    curNumber.append(curChar);
                    break;
                }
                case "Usual operator":
                case "Minus": {
                    second = Numbers.peek();
                    Numbers.pop();
                    first = Numbers.peek();
                    Numbers.pop();
                    Numbers.push(doOperation(first, second, curChar));
                    break;
                }
                case "Space": {
                    switch (whatIsIt(prevChar)) {
                        case "Digit": {
                            Numbers.push(Double.parseDouble(curNumber.toString()));
                            curNumber.delete(0, curNumber.length());
                        }
                    }
                }
            }
        }
        return Numbers.peek();
    }
}
