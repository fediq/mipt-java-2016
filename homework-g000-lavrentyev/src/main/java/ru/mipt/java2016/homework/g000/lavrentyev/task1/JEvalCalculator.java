package ru.mipt.java2016.homework.g000.lavrentyev.task1;

import net.sourceforge.jeval.EvaluationConstants;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Пример реализации калькулятора средствами JEval.
 *
 * @author Fedor S. Lavrentyev
 * @since 28.09.16
 */
public class JEvalCalculator implements Calculator {
    protected Evaluator buildEvaluator() {
        return new Evaluator(EvaluationConstants.SINGLE_QUOTE, false, false, false, false);
    }

    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }
        try {
            Evaluator evaluator = buildEvaluator();
            String result = evaluator.evaluate(expression);
            return Double.parseDouble(result);
        } catch (EvaluationException e) {
            throw new ParsingException("Invalid expression", e.getCause());
        }
    }
}
