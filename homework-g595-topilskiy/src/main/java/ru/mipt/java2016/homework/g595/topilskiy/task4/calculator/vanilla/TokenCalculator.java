package ru.mipt.java2016.homework.g595.topilskiy.task4.calculator.vanilla;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;

/**
 * Calculator that evaluates the expression by the way of tokens
 *
 * Expression is defined as the following (in extended Backus-Naur Form)
 *   expression = multiple, { ('+' | '-') multiple }
 *   multiple   = braced_expression, { ('*' | '/') braced_expression }
 *   braced_expression = '(' expression ')' | number_expression
 *   number_expression = '-' braced_expression | double_number
 *   double_number = '[1-9]' { '[0-9]' } [ '.' ] { '[0-9]' }+
 *
 * @author Artem K. Topilskiy
 * @since  16.12.16.
 */
public class TokenCalculator implements Calculator {
    /**
     *  Data
     */
    private final ArrayList<Token> tokens = new ArrayList<>();
    private int tokensIndex = 0;


    /**
     *  Private Token-generate Functions
     */
    private void parse(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Expression is null.");
        }

        for (int expressionIndex = 0; expressionIndex < expression.length(); ++expressionIndex) {
            Token.TokenType currentTokenType;
            Double currentNumber = null;

            if (Character.isWhitespace(expression.charAt(expressionIndex)) ||
                 Character.isSpaceChar(expression.charAt(expressionIndex))) {
                continue;
            }

            switch(expression.charAt(expressionIndex)) {
                case '+':
                    currentTokenType = Token.TokenType.PLUS;
                    break;

                case '-':
                    currentTokenType = Token.TokenType.MINUS;
                    break;

                case '*':
                    currentTokenType = Token.TokenType.MULTIPLY;
                    break;

                case '/':
                    currentTokenType = Token.TokenType.DIVIDE;
                    break;

                case '(':
                    currentTokenType = Token.TokenType.LEFT_BRACE;
                    break;

                case ')':
                    currentTokenType = Token.TokenType.RIGHT_BRACE;
                    break;

                default:
                    if (!Character.isDigit(expression.charAt(expressionIndex))) {
                        throw new ParsingException(String.format("Unexpected symbol at %d", expressionIndex));
                    }

                    boolean readDot = false;
                    int numberStartIndex = expressionIndex;
                    for (; expressionIndex < expression.length(); ++expressionIndex) {
                        Character currentCharacter = expression.charAt(expressionIndex);
                        if (currentCharacter == '.' && !readDot) {
                            readDot = true;
                        } else if (!Character.isDigit(currentCharacter)) {
                            break;
                        }
                    }

                    currentNumber = Double.parseDouble(expression.substring(numberStartIndex, expressionIndex));
                    --expressionIndex;
                    currentTokenType = Token.TokenType.NUMBER;
                    break;
            }

            if (currentTokenType != Token.TokenType.NUMBER) {
                tokens.add(new Token(currentTokenType));
            } else {
                tokens.add(new Token(currentNumber));
            }
        }
    }


    /**
     *  Private Token-read Functions
     */
    private void regressTokensIndex() {
        --tokensIndex;
    }

    private Token progressTokens() {
        if (tokensIndex >= tokens.size()) {
            return null;
        }

        return tokens.get(tokensIndex++);
    }


    /**
     *  Private Token-calculate Functions
     */
    private Double expression() throws ParsingException {
        Double result = multiple();

        for (Token token = progressTokens(); token != null; token = progressTokens()) {
            if (token.getType() == Token.TokenType.PLUS) {
                result += multiple();
            } else if (token.getType() == Token.TokenType.MINUS) {
                result -= multiple();
            } else {
                regressTokensIndex();
                break;
            }
        }

        return result;
    }

    private Double multiple() throws ParsingException {
        Double result = bracedExpression();

        for (Token token = progressTokens(); token != null; token = progressTokens()) {
            if (token.getType() == Token.TokenType.MULTIPLY) {
                result *= bracedExpression();
            } else if (token.getType() == Token.TokenType.DIVIDE) {
                result /= bracedExpression();
            } else {
                regressTokensIndex();
                break;
            }
        }

        return result;
    }

    private Double bracedExpression() throws ParsingException {
        Double result;

        Token token = progressTokens();
        if (token == null) {
            throw new ParsingException("Invalid amount of numbers.");
        }

        if (token.getType() == Token.TokenType.LEFT_BRACE) {
            result = expression();
            token = progressTokens();

            if (token == null || token.getType() != Token.TokenType.RIGHT_BRACE) {
                throw new ParsingException("Wrong number of left/right braces");
            }
        } else {
            regressTokensIndex();
            result = numberExpression();
        }

        return result;
    }

    private Double numberExpression() throws ParsingException {
        Double result;

        Token token = progressTokens();
        if (token == null) {
            throw new ParsingException("Invalid amount of numbers");
        }

        if (token.getType() == Token.TokenType.MINUS) {
            result = -expression();
        } else if (token.getType() == Token.TokenType.NUMBER) {
            result = token.getNumber();
        } else {
            throw new ParsingException("Invalid order of operations");
        }

        return result;
    }


    /**
     *  Public calculate interface
     */
    public double calculate(String expression) throws ParsingException {
        tokens.clear();
        tokensIndex = 0;

        parse(expression);
        Double result = expression();

        if (tokensIndex != tokens.size()) {
            throw new ParsingException("Invalid number of tokens (too many).");
        }

        return result;
    }
}
