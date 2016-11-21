package ru.mipt.java2016.seminars.seminar6;

import java.util.ArrayList;
import java.util.List;

public class ResurrectExample {
    private static final List<Zombies> ZOMBIES = new ArrayList<>();

    private static class Zombies {
        private final int num;

        public Zombies(int num) {
            this.num = num;
        }

        @Override
        protected void finalize() throws Throwable {
            System.out.println("Resurrect " + num);
            ZOMBIES.add(this);
        }

        @Override
        public String toString() {
            return "Zombies{" + "num=" + num + '}';
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            ZOMBIES.add(new Zombies(i));
        }
        for (int j = 0; j < 5; j++) {
            System.out.println("Zombies: " + ZOMBIES);
            ZOMBIES.clear();
            System.gc();
            Thread.sleep(100);
        }
    }
}
