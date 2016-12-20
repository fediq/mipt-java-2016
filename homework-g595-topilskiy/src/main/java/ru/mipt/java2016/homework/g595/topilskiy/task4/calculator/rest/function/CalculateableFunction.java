package ru.mipt.java2016.homework.g595.topilskiy.task4.calculator.rest.function;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.topilskiy.task4.calculator.vanilla.Token;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Calculator functions which can be evaluated in runtime
 *
 * Expression is defined as the following (in extended Backus-Naur Form)
 *   expression = multiple, { ('+' | '-') multiple }
 *   multiple   = braced_expression, { ('*' | '/') braced_expression }
 *   braced_expression = '(' expression ')' | combined_expression
 *   combined_expression = '-' braced_expression | double_number | function_call | variable
 *   function_call = function_name '(' function_parameters_called ')'
 *   function_parameters_called = expression { ',' expression }
 *
 *   double_number = '[1-9]' { '[0-9]' } [ '.' ] { '[0-9]' }+
 *   variable = '[a-zA-Z]' { '[a-zA-Z0-9]' }
 *   function_definition = function_name '(' function_parameters ')'
 *   function_parameters = variable { ',' variable }
 *
 * @author Artem K. Topilskiy
 * @since  16.12.16.
 */
public class CalculateableFunction implements IEvaluateableFunction {
    /**
     *  Global Function Data
     */
    private Map<String, IEvaluateableFunction> functions;
    private Map<String, Double> variables;

    /**
     *  Function Data
     */
    private final ArrayList<Token> tokens = new ArrayList<>();
    private int tokensIndex = 0;
    private String functionExpression;

    /**
     *  Function Parameter Data
     */
    private Map<Integer, String> parameterNumberToParameterString = new HashMap<>();
    private Map<String, Double> parameters = new HashMap<>();

    /**
     *  CONSTRUCTOR
     */
    public CalculateableFunction(String functionExpression, List<String> functionParameters,
                                 Map<String, IEvaluateableFunction> functions,
                                 Map<String, Double> variables) throws ParsingException {
        if (functionParameters != null) {
            for (int i = 0; i < functionParameters.size(); ++i) {
                parameterNumberToParameterString.put(i, functionParameters.get(i));
            }
        }

        this.functionExpression = functionExpression;
        this.functions = functions;
        this.variables = variables;

        parse(functionExpression);
    }

    /**
     *  Private Token-generate Functions
     */
    private void parse(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Expression is null.");
        }

        for (int expressionIndex = 0; expressionIndex < expression.length(); ++expressionIndex) {
            Token currentToken;

            if (Character.isWhitespace(expression.charAt(expressionIndex)) ||
                    Character.isSpaceChar(expression.charAt(expressionIndex))) {
                continue;
            }

            switch (expression.charAt(expressionIndex)) {
                case '+':
                    currentToken = new Token(Token.TokenType.PLUS);
                    break;

                case '-':
                    currentToken = new Token(Token.TokenType.MINUS);
                    break;

                case '*':
                    currentToken = new Token(Token.TokenType.MULTIPLY);
                    break;

                case '/':
                    currentToken = new Token(Token.TokenType.DIVIDE);
                    break;

                case '(':
                    currentToken = new Token(Token.TokenType.LEFT_BRACE);
                    break;

                case ')':
                    currentToken = new Token(Token.TokenType.RIGHT_BRACE);
                    break;

                case ',':
                    currentToken = new Token(Token.TokenType.COMMA);
                    break;

                default:
                    if (Character.isDigit(expression.charAt(expressionIndex))) {
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

                        Double currentNumber =
                                Double.parseDouble(expression.substring(numberStartIndex, expressionIndex));
                        --expressionIndex;
                        currentToken = new Token(currentNumber);

                    } else if (Character.isAlphabetic(expression.charAt(expressionIndex))) {
                        int nameStartIndex = expressionIndex;
                        for (; expressionIndex < expression.length(); ++expressionIndex) {
                            Character currentCharacter = expression.charAt(expressionIndex);
                            if (!Character.isAlphabetic(currentCharacter) &&
                                    !Character.isDigit(currentCharacter)  &&
                                    currentCharacter != '_') {
                                break;
                            }
                        }

                        currentToken = new Token(functionExpression.substring(nameStartIndex, expressionIndex));
                        --expressionIndex;
                    } else {
                        throw new ParsingException(String.format("Unexpected symbol at %d", expressionIndex));
                    }
                    break;
            }

            tokens.add(currentToken);
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
            result = combinedExpression();
        }

        return result;
    }

    private Double combinedExpression() throws ParsingException {
        Double result;

        Token token = progressTokens();
        if (token == null) {
            throw new ParsingException("Invalid amount of numbers");
        }

        if (token.getType() == Token.TokenType.MINUS) {
            result = -bracedExpression();
        } else if (token.getType() == Token.TokenType.NUMBER) {
            result = token.getNumber();
        } else if (token.getType() == Token.TokenType.NAME) {
            regressTokensIndex();
            result = namedExpression();
        } else {
            throw new ParsingException("Invalid order of operations");
        }

        return result;
    }

    private Double namedExpression() throws ParsingException {
        Double result;

        Token token = progressTokens();
        if (token == null) {
            throw new ParsingException("Invalid amount of numbers");
        }
        String tokenName = token.getName();

        Token nextToken = progressTokens();
        if (nextToken != null && nextToken.getType() == Token.TokenType.LEFT_BRACE) {
            if (!functions.containsKey(tokenName)) {
                throw new ParsingException("Unexpected symbol. Function call impossible.");
            }

            result = functionCall(tokenName);
        } else {
            if (nextToken != null) {
                regressTokensIndex();
            }

            if (parameters.containsKey(tokenName)) {
                result = parameters.get(tokenName);
            } else {
                if (!variables.containsKey(tokenName)) {
                    throw new ParsingException("Unexpected symbol. No such variable.");
                }
                result = variables.get(tokenName);
            }
        }

        return result;
    }

    private Double functionCall(String functionName) throws ParsingException {
        Double result;

        IEvaluateableFunction function = functions.get(functionName);
        List<Double> functionArguments = new ArrayList<>();

        Token token;
        do {
            token = progressTokens();

            if (token == null) {
                throw new ParsingException("Unexpected end of function expression.");
            } else {
                if (token.getType() == Token.TokenType.COMMA) {
                    throw new ParsingException("Unexpected comma - no parameter.");
                } else if (token.getType() == Token.TokenType.RIGHT_BRACE) {
                    break;
                } else {
                    regressTokensIndex();
                }
            }

            functionArguments.add(expression());

            token = progressTokens();
            if (token == null ||
                    (token.getType() != Token.TokenType.COMMA && token.getType() != Token.TokenType.RIGHT_BRACE)) {
                throw new ParsingException("Unexpected end of function expression.");
            }
        } while (token.getType() != Token.TokenType.RIGHT_BRACE);

        function.setArguments(functionArguments);
        result = function.evaluate();
        return result;
    }


    /**
     *  OVERRIDE
     */
    @Override
    public Double evaluate() throws ParsingException {
        tokensIndex = 0;
        Double result = expression();

        if (tokensIndex != tokens.size()) {
            throw new ParsingException("Invalid number of tokens (too many).");
        }

        return result;
    }

    @Override
    public void setArguments(List<Double> arguments) throws ParsingException {
        if (arguments.size() != parameterNumberToParameterString.size()) {
            throw new ParsingException("Number of arguments to be set is invalid.");
        }
        for (int argumentsIndex = 0; argumentsIndex < arguments.size(); ++argumentsIndex) {
            parameters.put(parameterNumberToParameterString.get(argumentsIndex), arguments.get(argumentsIndex));
        }
    }

    /**
     *  GETTERS
     */
    public String getFunctionExpression() {
        return functionExpression;
    }

    public List<String> getParameterList() {
        List<String> result = new ArrayList<>();

        List<Map.Entry<Integer, String>> entries =
                parameterNumberToParameterString.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .collect(Collectors.toList());

        for (Map.Entry<Integer, String> entry : entries) {
            result.add(entry.getValue());
        }
        return result;
    }
}
