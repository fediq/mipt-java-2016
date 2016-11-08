package ru.mipt.java2016.seminars.seminar6;

public class Sample {
    public int f1;
    protected int f2;
    private int f3;

    public Sample() {}

    private void doStuff() {
        System.out.println("doStuff()");
    }

    private void doStuff(int a) {
        System.out.print("doStuff(" + a + ")");
    }
}
