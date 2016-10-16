package ru.mipt.java2016.homework.g595.gusarova.task1;

/**
 * Created by Дарья on 11.10.2016.
 */
class SimpleStruct {
    private Double number; //хранится число или 1, если открывающая скобка, -1 закрывающая
    //или 1, если +, 2 -, 3  *, 4  /;
    private Boolean operator; //является ли +, -, *, /;
    private Boolean bracket;  //является ли скобкой;

    SimpleStruct(Boolean op, Boolean br, Double num) {
        operator = op;
        bracket = br;
        number = num;
    }

    Boolean isNumber() {
        return !bracket && !operator;
    }

    Boolean isBracket() {
        return bracket;
    }

    Boolean isOperator() {
        return operator;
    }

    Double getNumber() {
        return number;
    }
}