package ru.mipt.java2016.seminars.seminar5;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockExample {
    private final static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final static Lock readLock = readWriteLock.readLock();
    private final static Lock writeLock = readWriteLock.writeLock();

    private static void startReadingTasks(ExecutorService service) {
        for (int i = 0; i < 4; ++i) {
            service.execute(() -> {
                readLock.lock();
                try {
                    System.out.println("Reading by thread" + Thread.currentThread().getName());
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } finally {
                    readLock.unlock();
                }
            });
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool();

        startReadingTasks(service);

        service.execute(() -> {
            writeLock.lock();
            try {
                System.out.println("Writing by thread" + Thread.currentThread().getName());
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                writeLock.unlock();
            }
        });

        startReadingTasks(service);

        service.shutdown();
        service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }
}
