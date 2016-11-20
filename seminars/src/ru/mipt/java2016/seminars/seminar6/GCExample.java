package ru.mipt.java2016.seminars.seminar6;

public class GCExample {
    private static class A {
        @Override
        public void finalize() throws Throwable {
            super.finalize();
            System.out.println("finalized");
        }
    }

    public static void main(String[] args) {
        A a = new A();
        a = null;

        System.gc();
    }
}
