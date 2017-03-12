package ru.mipt.java2016.homework.g596.narsia.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import java.util.*;


public class MyCalculator implements Calculator{

    private String whatIsIt(Character Symbol) {
        switch (Symbol) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': {
                return "Digit";
            }
            case '.': {
                return "Point";
            }
            case '+':
            case '*':
            case '/': {
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
            case '\n': {
                return "Space";
            }
            case '~': {
                return "First";
            }
            default: {
                return "Ochen` ploxo";
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
                    case '(': {
                        return 1;
                    }
                    case '~': {
                        return 4;
                    }
                    case ')': {
                        return 5;
                    }
                }
            }
            case '+':
            case '-': {
                switch (second) {
                    case '*':
                    case '/':
                    case '(': {
                        return 1;
                    }
                    case '~':
                    case ')':
                    case '+':
                    case '-': {
                        return 2;
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
                    case '/': {
                        return 2;
                    }
                }
            }
            case '(': {
                switch (second) {
                    case '(':
                    case '+':
                    case '-':
                    case '*':
                    case '/': {
                        return 1;
                    }
                    case ')': {
                        return 3;
                    }
                    case '~': {
                        return 5;
                    }
                }
            }
        }
        return 0;
    }




    private void isAlmostValid(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null");
        }
        Character PrevSymbol = '~';
        Character CurSymbol;
        Character ImportantPrevSymbol = '~';
        int WasThereAPoint = 0;
        Boolean flagForSpaces = false;

        for (int i = 0; i < expression.length(); ++i) {
            CurSymbol = expression.charAt(i);
            switch (whatIsIt(CurSymbol)) {
                case "Digit": {
                    flagForSpaces = true;
                    switch (whatIsIt(PrevSymbol)) {
                        case "Space": {
                            switch (whatIsIt(ImportantPrevSymbol)) {
                                case "Digit": {
                                    throw new ParsingException("Space between digits");
                                }
                                case "Closing bracket": {
                                    throw new ParsingException("Closing bracket before digit");
                                }
                            }
                            break;
                        }
                        case "Closing bracket": {
                            throw new ParsingException("Closing bracket before digit");
                        }
                    }
                    ImportantPrevSymbol = CurSymbol;
                    PrevSymbol = CurSymbol;
                    break;
                }


                case "Point": {
                    flagForSpaces = true;
                    switch (whatIsIt(PrevSymbol)) {
                        case "Space": {
                            throw new ParsingException("Space before point");
                        }
                        case "Opening bracket":
                        case "Closing bracket": {
                            throw new ParsingException("Bracket before point");
                        }
                        case "Usual operator":
                        case "Minus": {
                            throw new ParsingException("Operator before point");
                        }
                        case "First": {
                            throw new ParsingException("Nothing before point");
                        }
                        case "Point": {
                            throw new ParsingException("Double point");
                        }
                        case "Digit": {
                            switch (WasThereAPoint) {
                                case 0: {
                                    WasThereAPoint = 1;
                                    break;
                                }
                                case 1: {
                                    throw new ParsingException("2 points in one number");
                                }
                            }
                            break;
                        }
                    }
                    ImportantPrevSymbol = CurSymbol;
                    PrevSymbol = CurSymbol;
                    break;
                }


                case "Usual operator": {
                    flagForSpaces = true;
                    switch (whatIsIt(ImportantPrevSymbol)) {
                        case "Usual operator": {
                            throw new ParsingException("Too many operators");
                        }
                        case "Opening bracket": {
                            throw new ParsingException("Operator after opening bracket");
                        }
                        case "First": {
                            throw new ParsingException("Nothing before operator");
                        }
                        case "Point": {
                            throw new ParsingException("Operator after point");
                        }
                        case "Digit": {
                            WasThereAPoint = 0;
                            break;
                        }
                    }
                    ImportantPrevSymbol = CurSymbol;
                    PrevSymbol = CurSymbol;
                    break;
                }


                case "Minus": {
                    flagForSpaces = true;
                    switch (whatIsIt(ImportantPrevSymbol)) {
                        case "Point": {
                            throw new ParsingException("Operator after point");
                        }
                        case "CLosing bracket": {
                            break;
                        }
                        case "First":
                        case "Opening bracket": {
                            break;
                        }
                        case "Digit": {
                            WasThereAPoint = 0;
                            break;
                        }
                    }
                    ImportantPrevSymbol = CurSymbol;
                    PrevSymbol = CurSymbol;
                    break;
                }


                case "Space": {
                    switch (whatIsIt(PrevSymbol)) {
                        case "Point": {
                            throw new ParsingException("Space after point");
                        }
                        case "Digit": {
                            WasThereAPoint = 0;
                            break;
                        }
                    }
                    PrevSymbol = CurSymbol;
                    break;
                }


                case "Opening bracket": {
                    flagForSpaces = true;
                    switch (whatIsIt(ImportantPrevSymbol)) {
                        case "Point": {
                            throw new ParsingException("Point before opening bracket");
                        }
                        case "Digit": {
                            throw new ParsingException("Digit before opening bracket");
                        }
                        case "Closing bracket": {
                            throw new ParsingException("Nothing in brackets");
                        }
                    }
                    ImportantPrevSymbol = CurSymbol;
                    PrevSymbol = CurSymbol;
                    break;
                }


                case "Closing bracket": {
                    flagForSpaces = true;
                    switch (whatIsIt(ImportantPrevSymbol)) {
                        case "Point": {
                            throw new ParsingException("Point before closing bracket");
                        }
                        case "Digit": {
                            WasThereAPoint = 0;
                            break;
                        }
                        case "Opening bracket": {
                            throw new ParsingException("Nothing between brackets");
                        }
                        case "First": {
                            throw new ParsingException("Closing bracket is first");
                        }
                        case "Usual operator":
                        case "Minus": {
                            throw new ParsingException("Operator before closing bracket");
                        }
                    }
                    ImportantPrevSymbol = CurSymbol;
                    PrevSymbol = CurSymbol;
                    break;
                }


                case "Ochen` ploxo":
                case "First": {
                    throw new ParsingException("Invalid symbol");
                }
            }
        }
        if (!flagForSpaces) {
            throw new ParsingException("Only spaces");
        }
    }


    private String removeSpaces(String expression) {
        String result = "";
        for (int cnt = 0; cnt < expression.length(); ++cnt) {
            if (!whatIsIt(expression.charAt(cnt)).equals("Space")) {
                result += expression.charAt(cnt);
            }
        }
        return result;
    }


    private String removeUnaryMinuses(String expressionWithoutSpaces) {
        expressionWithoutSpaces += '~';
        String result = "";
        Boolean flag = false;
        for (int cnt = 0; cnt < expressionWithoutSpaces.length(); ++cnt) {
            if (expressionWithoutSpaces.charAt(cnt) == '-') {
                if (cnt == 0) {
                    result += '0';
                }
                else {
                    switch (whatIsIt(expressionWithoutSpaces.charAt(cnt - 1))) {
                        case "Opening bracket": {
                            result += "0-";
                            continue;
                        }
                        case "Usual operator":
                        case "Minus": {
                            result += "(0-";
                            flag = true;
                            continue;
                        }
                    }
                }
            }
            if ((!whatIsIt(expressionWithoutSpaces.charAt(cnt)).equals("Digit")) &&
                    (!whatIsIt(expressionWithoutSpaces.charAt(cnt)).equals("Point")) &&
                    (cnt > 0) && (flag)) {
                result += ")";
                flag = false;
            }
            if (expressionWithoutSpaces.charAt(cnt) != '~') {
                result += expressionWithoutSpaces.charAt(cnt);
            }
        }
        return result;
    }


    private String getRPN(String expression) throws ParsingException {
        expression += '~';
        Character curSymbol;
        Character prevSymbol = '~';
        Stack<Character> Texas = new Stack<>();
        String California = "";
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
                            California += curSymbol;
                            ++cnt;
                            break;
                        }
                        default: {
                            California += " ";
                            California += curSymbol;
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
                    California += " ";
                    California += Texas.peek();
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
        String withoutSpaces = removeSpaces(expression);
        String withoutSpacesAndUnaryMinuses = removeUnaryMinuses(withoutSpaces);
        String RPN = getRPN(withoutSpacesAndUnaryMinuses);
        Stack<Double> Numbers = new Stack<>();
        Double first, second;
        String curNumber = "";
        Character curChar = '~';
        Character prevChar;
        for (int cnt = 0; cnt < RPN.length(); ++cnt) {
            prevChar = curChar;
            curChar = RPN.charAt(cnt);
            switch (whatIsIt(curChar)) {
                case "Digit":
                case "Point": {
                    curNumber += curChar;
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
                            Numbers.push(Double.parseDouble(curNumber));
                            curNumber = "";
                        }
                    }
                }
            }
        }
        return Numbers.peek();
    }
}
