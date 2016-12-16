package ru.mipt.java2016.homework.g594.kozlov.task2;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by Anatoly on 18.11.2016.
 */
public class BufferedQueue {
    BufferedQueue(FileWorker file) {
        this.file = file;
    }

    private FileWorker file;
    private Deque<String> queue = new ArrayDeque<>();
    private long currOffset = 0;

    public String getNext() {
        if (queue.isEmpty()) {
            buffQueue();
        }
        if (queue.isEmpty()) {
            return null;
        } else {
            String str = queue.peekFirst();
            queue.removeFirst();
            return str;
        }
    }

    void buffQueue() {
        file.close();
        file.moveToOffset(currOffset);
        String token = file.readNextToken();
        int i = 20;
        while (i > 0 && token != null) {
            queue.addLast(token);
            currOffset += 4 + token.length();
            i--;
            token = file.readNextToken();
        }
        file.close();
    }
}
