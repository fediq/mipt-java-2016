package ru.mipt.java2016.seminars.seminar5;

import java.util.Arrays;

public final class FalseSharing {
    public final static int NUM_THREADS = 1;
    public final static long ITERATIONS = 1000L * 1000L;

    private static volatile long[] items = new long[NUM_THREADS];

    private static class MyRunnable implements Runnable {
        private final int index;
        private final long leftIndex;
        private final long rightIndex;

        public MyRunnable(int index) {
            this.index = index;

            leftIndex = ITERATIONS / NUM_THREADS * index;
            rightIndex = (index != NUM_THREADS - 1)
                    ? ITERATIONS / NUM_THREADS * (index + 1)
                    : ITERATIONS;
        }

        @Override
        public void run() {
            for (long i = leftIndex; i < rightIndex; ++i) {
                ++items[index];
            }
        }
    }

    public static void main(final String[] args) throws Exception {
        final long start = System.nanoTime();
        runTest();
        System.out.println("duration = " + (System.nanoTime() - start));
    }

    private static void runTest() throws InterruptedException {
        Thread[] threads = new Thread[NUM_THREADS];

        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread(new MyRunnable(i));
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println(Arrays.stream(items).sum());
    }
}