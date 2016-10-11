package ru.mipt.java2016.homework.g595.gusarova.task1;

/**
 * Created by Дарья on 11.10.2016.
 */
class simple_struct
{
    public Double number; //хранится число или 1, если открывающая скобка, -1 закрывающая
    //или 1, если +, 2 -, 3  *, 4  /;
    public Boolean operator; //является ли +, -, *, /;
    public Boolean bracket;  //является ли скобкой;
    public simple_struct(Boolean op, Boolean br, Double num)
    {
        operator = op;
        bracket = br;
        number = num;
    }
    public Boolean isNumber()
    {
        if (!bracket && !operator)
            return true;
        else return false;
    }
}