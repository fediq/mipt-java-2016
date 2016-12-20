package ru.mipt.java2016.homework.g397.kulikov.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.base.task1.Calculator;

import java.util.ArrayList;

/**
 * @author aq
 * @since 05.12.16.
 */

public class QCalculator implements Calculator {

    private ArrayList<QToken> tokens = new ArrayList<>();

    private class Parser {
        private int currentPosition = 0;

        public void parseExpression(String expression) throws ParsingException {
            stripSpaces(expression);

            for( ; currentPosition < expression.length(); ) {
                tokens.add(getNextToken(expression));
                currentPosition++;

                stripSpaces(expression);
            }

            tokens.add(new QToken(QToken.TokType.END));
        }

        private QToken getNextToken(String expression) throws ParsingException {

            switch(expression.charAt(currentPosition)) {
                case '+':
                    return new QToken(QToken.TokType.ADD);

                case '-':
                    return new QToken(QToken.TokType.SUB);

                case '*':
                    return new QToken(QToken.TokType.MUL);

                case '/':
                    return new QToken(QToken.TokType.DIV);

                case '(':
                    return new QToken(QToken.TokType.LPAREN);

                case ')':
                    return new QToken(QToken.TokType.RPAREN);

                default:
                    if (!Character.isDigit(expression.charAt(currentPosition))) {
                        throw new ParsingException(String.format("Unknown symbol at %d", currentPosition));
                    }

                    boolean foundDot = false;
                    int startPosition = currentPosition;
                    for ( ; currentPosition < expression.length(); currentPosition++) {
                        Character c = expression.charAt(currentPosition);

                        if(c == '.' && !foundDot) {
                            foundDot = true;
                            continue;
                        }

                        if(!Character.isDigit(c))
                            break;
                    }

                    Double value = Double.parseDouble(expression.substring(startPosition, currentPosition));
                    currentPosition--;
                    return new QToken(value);
            }
        }

        private void stripSpaces(String expression) {
            while(currentPosition < expression.length() &&
                    (Character.isWhitespace(expression.charAt(currentPosition)) ||
                            Character.isSpaceChar(expression.charAt(currentPosition))))
                currentPosition++;
        }
    }

    /*
        Grammar:
        Result = E
        E = T+T | T-T | T
        T = P*P | P/P | P
        P = [-+]?N | [-+]?(E)
        N = number
     */

    private class Evaluator {
        private int currentToken = 0;

        public double evaluate() throws ParsingException {
            double result = getE();

            if (currentToken < tokens.size() - 1) {
                throw new ParsingException("Unexpected end of expression.");
            }

            return result;
        }

        private double getE() throws ParsingException {
            double val1 = getT();

            QToken token = getCurToken();

            while(token.getType() == QToken.TokType.ADD ||
                    token.getType() == QToken.TokType.SUB) {
                QToken opToken = token;
                currentToken++;

                double val2 = getT();

                if(opToken.getType() == QToken.TokType.ADD) {
                    val1 += val2;
                }
                if(opToken.getType() == QToken.TokType.SUB) {
                    val1 -= val2;
                }

                token = getCurToken();
            }

            return val1;
        }

        private double getT() throws ParsingException {
            double val1 = getP();

            QToken token = getCurToken();

            while(token.getType() == QToken.TokType.MUL ||
                    token.getType() == QToken.TokType.DIV) {
                QToken opToken = token;
                currentToken++;

                double val2 = getP();

                if(opToken.getType() == QToken.TokType.MUL) {
                    val1 *= val2;
                }
                if(opToken.getType() == QToken.TokType.DIV) {
                    val1 /= val2;
                }

                token = getCurToken();
            }

            return val1;
        }

        private double getP() throws ParsingException {
            QToken token = getCurToken();

            double result = 1;

            if(token.getType() == QToken.TokType.SUB) {
                result = -1;
                token = getNextToken();
            }
            else {
                if(token.getType() == QToken.TokType.ADD) token = getNextToken();
            }

            if(token.getType() == QToken.TokType.LPAREN) {
                currentToken++;

                result *= getE();

                token = getCurToken();
                if(token.getType() != QToken.TokType.RPAREN) {
                    throw new ParsingException("Unpaired RPAREN in getP().");
                }
                currentToken++;

                return result;
            }
            else {
                result *= getN();

                return result;
            }
        }

        private double getN() throws ParsingException {
            QToken token = getCurToken();
            currentToken++;

            if (token.getType() != QToken.TokType.NUMBER) {
                throw new ParsingException("Not a number in getN().");
            }

            double result = token.getNumber();

            return result;
        }

        private QToken getNextToken() {
            return tokens.get(++currentToken);
        }

        private QToken getCurToken() {
            return tokens.get(currentToken);
        }
    }

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }
        Parser parser = new Parser();
        parser.parseExpression(expression);

//        for( int i = 0; i < tokens.size(); i++ ) {
//            System.out.println(tokens.get(i).getType() + " " + tokens.get(i).getNumber());
//        }

        Evaluator eval = new Evaluator();
        double result = eval.evaluate();

        return result;
    }

}
