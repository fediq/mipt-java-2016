package ru.mipt.java2016.homework.g595.murzin.task3;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;

/**
 * Created by dima on 05.11.16.
 */
class BufferedRandomAccessFile {
    private static class MyBufferedInputStream extends BufferedInputStream {
        public MyBufferedInputStream(InputStream in) {
            super(in);
        }

        public long getPositionInBuffer() {
            return pos;
        }

        public long getBufferSize() {
            return count;
        }
    }

    public RandomAccessFile randomAccessFile;

    public MyBufferedInputStream bufferedInputStream;

    public BufferedRandomAccessFile(File storageFile) throws FileNotFoundException {
        randomAccessFile = new RandomAccessFile(storageFile, "rw");
        bufferedInputStream = new MyBufferedInputStream(Channels.newInputStream(randomAccessFile.getChannel()));
    }

    public void seek(long fileOffset) {
        // randomAccessFile             $..........[.........#........)...........^
        // buffer                                  [.........#........)
        // buffer[positionInBuffer]                          #
        // bufferSize                              <------------------>
        // positionInBuffer                        <--------->
        // endPosition                  <----------------------------->

        long bufferSize = bufferedInputStream.getBufferSize();
        long positionInBuffer = bufferedInputStream.getPositionInBuffer();
        long endPosition = randomAccessFile.getChannel().position();
    }
}
