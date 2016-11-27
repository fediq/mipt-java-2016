package ru.mipt.java2016.homework.g595.romanenko.task4;

import org.springframework.web.bind.annotation.RestController;
import ru.mipt.java2016.homework.g595.romanenko.task4.base.BaseCalculatorController;
import ru.mipt.java2016.homework.g595.romanenko.task4.base.ICalculator;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task4
 *
 * @author Ilya I. Romanenko
 * @since 26.11.16
 **/
@RestController
public class CalculatorController extends BaseCalculatorController {

    @Override
    protected ICalculator createCalculator() {
        return new RestCalculator();
    }
}
