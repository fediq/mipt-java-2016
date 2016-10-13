package ru.mipt.java2016.homework.g596.pockonechny.task1;

/**
 * Created by celidos on 13.10.16.
 */
public class RpnElement {
    public Character op;            // Определяет тип содержимого: число или операция
    public double x;            //  Если число, то чему оно равно
    public Boolean rpriority;     // Если операция, является ли она правосторонней

    RpnElement(char _op, double _x, boolean _priority) {
        op = _op;
        x =_x;
        rpriority = _priority;
    }
}
