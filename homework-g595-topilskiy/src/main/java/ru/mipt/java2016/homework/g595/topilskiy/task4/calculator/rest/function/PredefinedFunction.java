package ru.mipt.java2016.homework.g595.topilskiy.task4.calculator.rest.function;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.*;

/**
 * Calculator functions that are agreed upon to be predefined
 *
 * @author Artem K. Topilskiy
 * @since  16.12.16.
 */
public class PredefinedFunction implements IEvaluateableFunction {
    /**
     *  PUBLIC STATIC DATA
     */
    public enum PredefinedFunctionType {
        SIN, COS, TG, SQRT, POW, ABS, SIGN, LOG, LOG2, RND, MAX, MIN
    }

    public static final Map<PredefinedFunctionType, Integer> PREDEFINED_FUNCTION_TYPE_NUM_ARGUMENTS_MAP;

    static {
        Map<PredefinedFunctionType, Integer> predefinedFunctionTypeNumArguments = new HashMap<>();
        predefinedFunctionTypeNumArguments.put(PredefinedFunctionType.SIN,  1);
        predefinedFunctionTypeNumArguments.put(PredefinedFunctionType.COS,  1);
        predefinedFunctionTypeNumArguments.put(PredefinedFunctionType.TG,   1);
        predefinedFunctionTypeNumArguments.put(PredefinedFunctionType.SQRT, 1);
        predefinedFunctionTypeNumArguments.put(PredefinedFunctionType.POW,  2);
        predefinedFunctionTypeNumArguments.put(PredefinedFunctionType.ABS,  1);
        predefinedFunctionTypeNumArguments.put(PredefinedFunctionType.SIGN, 1);
        predefinedFunctionTypeNumArguments.put(PredefinedFunctionType.LOG,  1);
        predefinedFunctionTypeNumArguments.put(PredefinedFunctionType.LOG2, 1);
        predefinedFunctionTypeNumArguments.put(PredefinedFunctionType.RND,  0);
        predefinedFunctionTypeNumArguments.put(PredefinedFunctionType.MAX,  2);
        predefinedFunctionTypeNumArguments.put(PredefinedFunctionType.MIN,  2);
        PREDEFINED_FUNCTION_TYPE_NUM_ARGUMENTS_MAP =
                Collections.unmodifiableMap(predefinedFunctionTypeNumArguments);
    }

    /**
     *  PRIVATE STATIC DATA
     */
    private static final Random RANDOM_NUMBER_GENERATOR = new Random(1337);


    /**
     *  DATA
     */
    private final PredefinedFunctionType functionType;
    private final List<Double> arguments = new ArrayList<>();


    /**
     *  CONSTRUCTOR
     */
    public PredefinedFunction(PredefinedFunctionType functionType) {
        this.functionType = functionType;
        for (int numArgumentsCounter = 0;
                 numArgumentsCounter < PREDEFINED_FUNCTION_TYPE_NUM_ARGUMENTS_MAP.get(functionType);
               ++numArgumentsCounter) {
            arguments.add(0.0);
        }
    }


    /**
     *  INTERFACE: IEvaluateableFunction
     */
    @Override
    public Double evaluate() throws ParsingException {
        Double result = 0.0;

        switch (functionType) {
            case SIN:
                result = Math.sin(arguments.get(0));
                break;
            case COS:
                result = Math.cos(arguments.get(0));
                break;
            case TG:
                result = Math.tan(arguments.get(0));
                break;
            case SQRT:
                result = Math.sqrt(arguments.get(0));
                break;
            case POW:
                result = Math.pow(arguments.get(0), arguments.get(1));
                break;
            case ABS:
                result = Math.abs(arguments.get(0));
                break;
            case SIGN:
                result = Math.signum(arguments.get(0));
                break;
            case LOG:
                result = Math.log(arguments.get(0));
                break;
            case LOG2:
                result = Math.log(arguments.get(0)) / Math.log(2);
                break;
            case RND:
                synchronized (RANDOM_NUMBER_GENERATOR) {
                    result = RANDOM_NUMBER_GENERATOR.nextDouble();
                }
                break;
            case MAX:
                result = Math.max(arguments.get(0), arguments.get(1));
                break;
            case MIN:
                result = Math.min(arguments.get(0), arguments.get(1));
                break;
            default:
                break;
        }

        return result;
    }

    @Override
    public void setArguments(List<Double> arguments) throws ParsingException {
        if (this.arguments.size() != arguments.size()) {
            throw new ParsingException("Number of arguments to be set is invalid.");
        }
        for (int argumentsIndex = 0; argumentsIndex < arguments.size(); ++argumentsIndex) {
            this.arguments.set(argumentsIndex, arguments.get(argumentsIndex));
        }
    }

    @Override
    public boolean isPredefined() {
        return true;
    }
}
