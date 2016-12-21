package ru.mipt.java2016.homework.g595.manucharyan.task4;

/**
 * Created by op on 17.12.2016.
 */
public class Token {

    public Token(RESTCalc.Symbol symbol) {
        this.symbol = symbol;
        valency = getValencyForOperator(symbol);
    }

    public Token(RESTCalc.Symbol symbol, double value) {
        this(symbol);
        this.value = value;
    }

    public Token(RESTCalc.Symbol symbol, String name, int valency) {
        this(symbol);
        this.name = name;
        this.valency = valency;
    }

    private double value; // for numbers
    private String name; // for functions and variables
    private int valency = 0; // for functions
    private RESTCalc.Symbol symbol = RESTCalc.Symbol.NONE;

    private int getValencyForOperator(RESTCalc.Symbol s) {
        switch (s) {
            case ADD:
                return 2;
            case UNOADD:
                return 1;
            case SUB:
                return 2;
            case UNOSUB:
                return 1;
            case MUL:
                return 2;
            case DIV:
                return 2;
            default:
                return 0;
        }
    }

    //get functions
    public double getValue() {return value;}
    public String getName() {return name;}
    public int getValency() {return valency;}
    public RESTCalc.Symbol getSymbol() {return symbol;}
}
