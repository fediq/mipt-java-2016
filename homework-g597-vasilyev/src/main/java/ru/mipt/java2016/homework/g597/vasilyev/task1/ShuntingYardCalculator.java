package ru.mipt.java2016.homework.g597.vasilyev.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.*;

/**
 * Created by mizabrik on 08.10.16.
 * Implementation using Dijkstra shunting algorithm with two stacks.
 */
class ShuntingYardCalculator implements ExtendableCalculator {
    private static final Map<Character, Operator> OPERATORS = new HashMap<>();
    private static final Map<String, BuiltinCommand> BUILTINS = new HashMap<>();

    static {
        OPERATORS.put('+', Operator.ADD);
        OPERATORS.put('-', Operator.SUBTRACT);
        OPERATORS.put('*', Operator.MULTIPLY);
        OPERATORS.put('/', Operator.DIVIDE);

        BUILTINS.put("sin", new BuiltinCommand((Double[] args) -> Math.sin(args[0]), 1));
        BUILTINS.put("cos", new BuiltinCommand((Double[] args) -> Math.cos(args[0]), 1));
        BUILTINS.put("tg", new BuiltinCommand((Double[] args) -> Math.tan(args[0]), 1));
        BUILTINS.put("sqrt", new BuiltinCommand((Double[] args) -> Math.sqrt(args[0]), 1));
        BUILTINS.put("pow", new BuiltinCommand((Double[] args) -> Math.pow(args[0], args[1]), 2));
        BUILTINS.put("abs", new BuiltinCommand((Double[] args) -> Math.abs(args[0]), 1));
        BUILTINS.put("sign", new BuiltinCommand((Double[] args) -> Math.signum(args[0]), 1));
        BUILTINS.put("log", new BuiltinCommand((Double[] args) -> Math.log(args[1]) / Math.log(args[0]), 2));
        BUILTINS.put("log2", new BuiltinCommand((Double[] args) -> Math.log(args[0]) / Math.log(2), 1));
        BUILTINS.put("log2", new BuiltinCommand((Double[] args) -> Math.log(args[0]) / Math.log(2), 1));
        BUILTINS.put("rnd", new BuiltinCommand((Double[] args) -> Math.log(args[0]) / Math.log(2), 1));
        BUILTINS.put("max", new BuiltinCommand((Double[] args) -> Math.max(args[0], args[1]), 2));
        BUILTINS.put("min", new BuiltinCommand((Double[] args) -> Math.min(args[0], args[1]), 2));
    }

    public static void main(String[] args) {
        try {
            ExtendableCalculator calc = new ShuntingYardCalculator();
            Scanner s = new Scanner(System.in);
            s.useLocale(Locale.US); // use dots for fractions
            s.useDelimiter("[,() \n]+");
            Map<String, Command> definitions = new HashMap<>();
            Scope scope = new MapScope(definitions);

            while (s.hasNext()) {
                String command = s.next();

                if (command.equals("var")) {
                    String variable = s.next();
                    s.next("=");
                    Double value = s.nextDouble();
                    definitions.put(variable, new PushNumberCommand(value));
                } else if (command.equals("def")) {
                    String function = s.next();
                    ArrayList<String> functionArgs = new ArrayList<>();
                    while (!s.hasNext("=")) {
                        functionArgs.add(s.next());
                    }
                    s.next("=");
                    definitions.put(function, new UserCommand(s.nextLine(), functionArgs.toArray(new String[0]),
                            calc, scope));
                } else if (command.equals("eval")) {
                    System.out.println(calc.calculate(s.nextLine(), scope));
                } else {
                    System.out.println(command + ": no such command");
                    s.nextLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Calculate expression.
    public double calculate(String expression) throws ParsingException {
        return calculate(expression, new MapScope(new HashMap<>()));
    }

    @Override
    public boolean supportsFunction(String name) {
        return BUILTINS.containsKey(name);
    }

    // Calculate expression with custom scope
    public double calculate(String expression, Scope scope) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }

        ArrayList<Command> commands = parse(expression, scope);
        if (commands.size() == 0) {
            throw new ParsingException("Empty expression");
        }

        return evaluate(commands);
    }


    // Tokenize expression
    public ArrayList<Command> parse(String expression, Scope scope) throws ParsingException {
        ArrayList<Command> result = new ArrayList<>();
        Stack<Command> commandStack = new Stack<>();
        ExpressionTokenizer tokenizer = new ExpressionTokenizer(expression, OPERATORS);

        int bracketBalance = 0;
        boolean gotOperand = false;
        ExpressionTokenizer.TokenType type;
        for (type = tokenizer.peekNextType(); type != null; type = tokenizer.peekNextType()) {
            switch (type) {
                case NUMBER:
                    result.add(new PushNumberCommand(tokenizer.nextNumber()));
                    popFunctions(result, commandStack);

                    gotOperand = true;
                    break;
                case OPERATOR:
                    Operator operator = tokenizer.nextOperator();
                    if (!gotOperand) {
                        switch (operator) {
                            case ADD:
                                operator = Operator.UNARY_PLUS;
                                break;
                            case SUBTRACT:
                                operator = Operator.UNARY_MINUS;
                                break;
                            default:
                                throw new ParsingException("No operand for operator");
                        }
                    } else {
                        popFunctions(result, commandStack);
                    }

                    while (!commandStack.empty() && commandStack.peek() instanceof Operator) {
                        Operator previousOperator = (Operator) commandStack.peek();
                        if (previousOperator.priority < operator.priority
                                || (operator.hasLeftAssociativity && previousOperator.priority == operator.priority)) {
                            result.add(previousOperator);
                            commandStack.pop();
                        } else {
                            break;
                        }
                    }
                    commandStack.push(operator);

                    gotOperand = false;
                    break;
                case OPENING_BRACKET:
                    ++bracketBalance;
                    tokenizer.nextBracket();
                    commandStack.push(null);

                    gotOperand = false;
                    break;
                case CLOSING_BRACKET:
                    if (bracketBalance == 0) {
                        throw new ParsingException("Bad bracket balance");
                    } else if (!gotOperand) {
                        throw new ParsingException("Empty brackets");
                    }
                    --bracketBalance;
                    tokenizer.nextBracket();

                    while (commandStack.peek() != null) {
                        result.add(commandStack.pop());
                    }
                    commandStack.pop();
                    popFunctions(result, commandStack);

                    gotOperand = true;
                    break;
                case IDENTIFIER:
                    String identifier = tokenizer.nextIdentifier();
                    if (BUILTINS.containsKey(identifier)) {
                        commandStack.push(new FunctionCommand(BUILTINS.get(identifier)));
                    } else if (scope.hasCommand(identifier)) {
                        commandStack.push(new FunctionCommand(scope.getCommand(identifier)));
                    } else {
                        throw new ParsingException("Unknown identifier " + identifier);
                    }

                    gotOperand = true; // well, possibly
                    break;
                case COMMA:
                    tokenizer.nextComma();
                    while (!commandStack.empty() && commandStack.peek() != null) {
                        result.add(commandStack.pop());
                    }
                    if (commandStack.empty()) {
                        throw new ParsingException("Misplaced comma");
                    }

                    gotOperand = false;
                    break;
                default:
                    throw new ParsingException("Illegal character");
            }
        }
        if (bracketBalance != 0) {
            throw new ParsingException("Bad bracket balance");
        }

        while (!commandStack.empty()) {
            result.add(commandStack.pop());
        }

        return result;
    }

    private void popFunctions(ArrayList<Command> result, Stack<Command> stack) {
        while (!stack.empty() && stack.peek() instanceof FunctionCommand) {
            result.add(stack.pop());
        }
    }

    // Get result of expression coded by consequence of tokens.
    private double evaluate(ArrayList<Command> commands) throws ParsingException {
        Stack<Double> stack = new Stack<>();

        for (Command command : commands) {
            command.apply(stack);
        }

        if (stack.size() != 1) {
            throw new ParsingException("Illegal expression");
        }

        return stack.pop();
    }
}
