package ru.mipt.java2016.homework.g595.murzin.task3;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by dima on 05.11.16.
 */
public class LSMBackgroundThread extends Thread {

    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    volatile private boolean running = true;
    volatile private RuntimeException savedException;

    @Override
    public void run() {
        while (running && !queue.isEmpty()) {
            try {
                Runnable task = queue.take();
                try {
                    task.run();
                } catch (RuntimeException e) {
                    savedException = e;
                    running = false;
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void submit(Runnable task) {
        checkForException();
        queue.add(task);
    }

    private void checkForException() {
        if (savedException != null) {
            throw new RuntimeException("Exception occur in background thread", savedException);
        }
    }

    public void shutdown() {
        checkForException();
        running = false;
        try {
            join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Can't shutdown", e);
        }
        checkForException();
    }
}
