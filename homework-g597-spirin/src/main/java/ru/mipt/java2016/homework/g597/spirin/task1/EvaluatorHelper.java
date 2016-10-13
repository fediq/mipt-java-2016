package ru.mipt.java2016.homework.g597.spirin.task1;


import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by whoami on 10/13/16.
 */

// Algorithm based on grammar expressions
class EvaluatorHelper {

    // Class data members
    private int pos;
    private char ch;
    private final String expression;

    // Constructor
    EvaluatorHelper(String expression) {
        this.expression = expression;
        pos = -1;
    }

    // Access to next character
    private void getNextChar() throws ParsingException {
        if (++pos < expression.length()) {
            ch = expression.charAt(pos);
        } else {
            // Just to stop execution
            ch = '&';
        }
    }

    // Wait for expected character
    private boolean tryCaptureChar(int charToBeCaptured) throws ParsingException {
        while (ch == ' ') {
            getNextChar();
        }

        if (ch == charToBeCaptured) {
            getNextChar();
            return true;
        }
        return false;
    }

    // Main logic function
    double evaluate() throws ParsingException {
        getNextChar();

        double result = processExpression();
        if (pos < expression.length()) {
            throw new ParsingException("Unexpected appearance of: " + ch);
        }

        return result;
    }

    // Evaluation the whole expression
    private double processExpression() throws ParsingException {
        double result = processTerm();

        while (true) {
            if (tryCaptureChar('+')) {
                result += processTerm();
            } else if (tryCaptureChar('-')) {
                result -= processTerm();
            } else {
                break;
            }
        }
        return result;
    }

    // Evaluation subterm of the expression
    private double processTerm() throws ParsingException {
        double result = processFactor();

        while (true) {
            if (tryCaptureChar('*')) {
                result *= processFactor();
            } else if (tryCaptureChar('/')) {
                result /= processFactor();
            } else {
                break;
            }
        }

        return result;
    }

    // Evaluation factors of the expression
    private double processFactor() throws ParsingException {
        if (tryCaptureChar('-')) {
            return -processFactor();
        }

        double result;
        int startPos = this.pos;

        if (tryCaptureChar('(')) {
            result = processExpression();
            tryCaptureChar(')');
        } else if ((ch >= '0' && ch <= '9') || ch == '.') {

            while ((ch >= '0' && ch <= '9') || ch == '.') {
                getNextChar();
            }

            // Number of points in a string
            // Dirty hack :)
            int countPoints = expression.substring(startPos, this.pos).length() -
                    expression.substring(startPos, this.pos).replace(".", "").length();

            if (countPoints > 1) {
                throw new ParsingException("Number with many points found");
            }

            result = Double.parseDouble(expression.substring(startPos, this.pos));
        } else {
            throw new ParsingException("Unexpected appearance of: " + ch);
        }

        return result;
    }

}
