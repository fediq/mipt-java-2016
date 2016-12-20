package ru.mipt.java2016.homework.g595.romanenko.task4;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.romanenko.task1.Token;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task4
 *
 * @author Ilya I. Romanenko
 * @since 26.11.16
 *
 * <h>Defenitions</h>
 * <p>
 * func_def = name '(' params_def ')'
 * params_def = variable { ',' variable }
 * number = '[0-9]' { '[0-9]' }
 * variable = '[a-zA-Z]' {'[a-zA-Z0-9_]'}
 * </p>
 * <h>Expression</h>
 * <p>
 * exp = mul {('+' | '-') mul}
 * mul = braces_exp {[('*' | '/')] braces_exp}
 * braces_exp = '(' exp ')' | comb_exp
 * comb_exp = '-' braces_exp | number | func_сall | variable
 * func_call = name '(' params_call ')'
 * params_call = exp { ',' exp }
 * </p>
 **/

@SuppressWarnings("Duplicates")
public class Function implements IEvaluateFunction {

    /**
     * Parsed body to tokens
     */
    private List<Token> tokens = new ArrayList<>();
    private Integer currentTokenNumber;
    private String body;

    /**
     * Params table
     */
    private Map<Integer, String> paramsNumberToParam = new HashMap<>();
    private Map<String, Double> params = new HashMap<>();

    /**
     * Global Tables
     */
    private Map<String, IEvaluateFunction> functionTable;
    private Map<String, Double> variableTable;

    public Function(String expression,
                    List<String> params,
                    Map<String, IEvaluateFunction> functionTable,
                    Map<String, Double> variableTable) throws ParsingException {
        if (params != null) {
            for (int i = 0; i < params.size(); ++i) {
                paramsNumberToParam.put(i, params.get(i));
            }
        }
        this.functionTable = functionTable;
        this.variableTable = variableTable;
        this.body = expression;
        parse(expression);
    }

    private void parse(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null body");
        }
        for (int i = 0; i < expression.length(); i++) {
            Token currentToken;

            if (Character.isWhitespace(expression.charAt(i)) || Character.isSpaceChar(expression.charAt(i))) {
                continue;
            }

            switch (expression.charAt(i)) {
                case '*':
                    currentToken = new Token(Token.TokenType.MUL);
                    break;

                case '/':
                    currentToken = new Token(Token.TokenType.DIVIDE);
                    break;

                case '+':
                    currentToken = new Token(Token.TokenType.PLUS);
                    break;

                case '-':
                    currentToken = new Token(Token.TokenType.MINUS);
                    break;

                case '(':
                    currentToken = new Token(Token.TokenType.LEFT_BRACES);
                    break;

                case ')':
                    currentToken = new Token(Token.TokenType.RIGHT_BRACES);
                    break;

                case ',':
                    currentToken = new Token(Token.TokenType.COMMA);
                    break;

                default:
                    if (Character.isDigit(expression.charAt(i))) {

                        boolean hasFoundDot = false;
                        int start = i;
                        for (; i < expression.length(); i++) {
                            Character chr = expression.charAt(i);
                            if (chr == '.' && !hasFoundDot) {
                                hasFoundDot = true;
                                continue;
                            }
                            if (!Character.isDigit(chr)) {
                                i--;
                                break;
                            }
                        }
                        if (i == expression.length()) {
                            i--;
                        }
                        currentToken = new Token(Double.parseDouble(expression.substring(start, i + 1)));

                    } else if (Character.isAlphabetic(expression.charAt(i))) {

                        int start = i;
                        for (; i < expression.length(); i++) {
                            Character chr = expression.charAt(i);
                            if (!Character.isAlphabetic(chr) && !Character.isDigit(chr) && chr != '_') {
                                i--;
                                break;
                            }
                        }
                        if (i == expression.length()) {
                            i--;
                        }
                        currentToken = new Token(expression.substring(start, i + 1));

                    } else {
                        throw new ParsingException(String.format("Unknown symbol at %d", i));
                    }

                    break;
            }
            tokens.add(currentToken);
        }
    }

    private void returnToken() {
        currentTokenNumber -= 1;
    }

    private Token getToken() {
        if (currentTokenNumber >= tokens.size()) {
            return null;
        }
        Token result = tokens.get(currentTokenNumber);
        currentTokenNumber += 1;
        return result;
    }

    private Double expression() throws ParsingException {
        Double result = mul();
        Token token;
        do {
            token = getToken();
            if (token == null) {
                break;
            }
            if (token.getType() == Token.TokenType.PLUS) {
                result += mul();
            } else if (token.getType() == Token.TokenType.MINUS) {
                result -= mul();
            } else {
                returnToken();
                break;
            }
        } while (true);
        return result;
    }

    private Double mul() throws ParsingException {
        Double result = bracesExpression();
        Token token;
        do {
            token = getToken();
            if (token == null) {
                break;
            }
            if (token.getType() == Token.TokenType.MUL) {
                result *= bracesExpression();
            } else if (token.getType() == Token.TokenType.DIVIDE) {
                result /= bracesExpression();
            } else {
                returnToken();
                break;
            }
        } while (true);
        return result;
    }

    private Double bracesExpression() throws ParsingException {
        Token token = getToken();
        Double result;
        if (token == null) {
            throw new ParsingException("Not enough tokens");
        }

        if (token.getType() == Token.TokenType.LEFT_BRACES) {
            result = expression();
            token = getToken();
            if (token == null || token.getType() != Token.TokenType.RIGHT_BRACES) {
                throw new ParsingException("Wrong amount of braces");
            }
        } else {
            returnToken();
            result = compoundExpr();
        }
        return result;
    }

    private Double compoundExpr() throws ParsingException {
        Token token = getToken();
        Double result;
        if (token == null) {
            throw new ParsingException("Not enough tokens");
        }
        if (token.getType() == Token.TokenType.MINUS) {
            result = -bracesExpression();
        } else if (token.getType() == Token.TokenType.NUMBER) {
            result = token.getNumber();
        } else if (token.getType() == Token.TokenType.NAME) {
            returnToken();
            result = functionExpr();
        } else {
            throw new ParsingException("Unclear order");
        }
        return result;
    }

    private Double functionExpr() throws ParsingException {
        Token token = getToken();
        Double result;
        if (token == null) {
            throw new ParsingException("Not enough tokens");
        }

        Token nextToken = getToken();

        if (nextToken != null && nextToken.getType() == Token.TokenType.LEFT_BRACES) {
            if (!functionTable.containsKey(token.getName())) {
                throw new ParsingException("Unexpected symbol, no such function.");
            }
            result = functionCall(token.getName());
        } else {
            if (nextToken != null) {
                returnToken();
            }

            if (params.containsKey(token.getName())) {
                result = params.get(token.getName());
            } else {
                if (!variableTable.containsKey(token.getName())) {
                    throw new ParsingException("Unexpected symbol, no such variable.");
                }
                result = variableTable.get(token.getName());
            }
        }
        return result;
    }

    private Double functionCall(String functionName) throws ParsingException {
        Double result;
        IEvaluateFunction function = functionTable.get(functionName);
        List<Double> args = new ArrayList<>();
        Token token;
        do {
            token = getToken();
            if (token == null) {
                throw new ParsingException("End of body");
            } else {
                if (token.getType() == Token.TokenType.COMMA) {
                    throw new ParsingException("Empty argument");
                } else if (token.getType() == Token.TokenType.RIGHT_BRACES) {
                    break;
                } else {
                    returnToken();
                }
            }
            args.add(expression());
            token = getToken();
            if (token == null ||
                    (token.getType() != Token.TokenType.COMMA && token.getType() != Token.TokenType.RIGHT_BRACES)) {
                throw new ParsingException("Unexpected end of function call");
            }

        } while (token.getType() != Token.TokenType.RIGHT_BRACES);

        function.setArgs(args);
        result = function.evaluate();
        return result;
    }

    public Double evaluate() throws ParsingException {
        currentTokenNumber = 0;

        Double result = expression();
        if (currentTokenNumber != tokens.size()) {
            throw new ParsingException("Too much tokens");
        }
        return result;
    }

    public String getBody() {
        return body;
    }

    public List<String> getParams() {
        List<String> result = new ArrayList<>();
        List<Map.Entry<Integer, String>> entries = paramsNumberToParam.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .collect(Collectors.toList());
        for (Map.Entry<Integer, String> entry : entries) {
            result.add(entry.getValue());
        }
        return result;
    }

    public void setArgs(List<Double> args) throws ParsingException {

        if (args.size() != paramsNumberToParam.size()) {
            throw new ParsingException("Amount of function's args doesn't match.");
        }

        for (int i = 0; i < args.size(); ++i) {
            params.put(paramsNumberToParam.get(i), args.get(i));
        }
    }
}
