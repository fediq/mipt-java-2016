package ru.mipt.java2016.seminars.seminar5;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WithoutSynchronization {
    public static class Counter {
        private long count = 0;

        // Синхронизировать можно с:
        // 1. synchronized void increment() {...}
        // 2. ReentrantLock
        // 3. AtomicLong count

        public void increment() {
            ++count;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final Counter counter = new Counter();

        ExecutorService service = Executors.newCachedThreadPool();

        for (int i = 0; i < 2; ++i) {
            service.execute(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int k = 0; k < 100000; ++k) {
                    counter.increment();
                }
            });
        }

        service.shutdown();
        service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        System.out.println(counter.count);
    }
}
