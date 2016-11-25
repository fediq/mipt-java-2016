package ru.mipt.java2016.homework.g595.murzin.task3slow;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;

/**
 * Created by dima on 05.11.16.
 */
public class BufferedRandomAccessFile implements Closeable {

    public static final int BUFFER_SIZE = 8192;

    private RandomAccessFile randomAccessFile;
    private HackedBufferedInputStream bufferedInputStream;
    private DataInputStream dataInputStream;
    private FileChannel fileChannel;

    public BufferedRandomAccessFile(File storageFile) throws FileNotFoundException {
        randomAccessFile = new RandomAccessFile(storageFile, "rw");
        fileChannel = randomAccessFile.getChannel();
        createBufferedInputStream();
    }

    private void createBufferedInputStream() {
        bufferedInputStream = new HackedBufferedInputStream(
                Channels.newInputStream(randomAccessFile.getChannel()), BUFFER_SIZE);
        dataInputStream = new DataInputStream(bufferedInputStream);
    }

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public long fileLength() throws IOException {
        return randomAccessFile.length();
    }

    public void seek(long fileOffset) throws IOException {
        // randomAccessFile             $..........[.........#........)...........^
        // buffer                                  [.........#........)
        // buffer[positionInBuffer]                          #
        // bufferSize                              <------------------>
        // positionInBuffer                        <--------->
        // endPosition                  <----------------------------->
        // startPosition                <---------->

        long bufferSize = bufferedInputStream.getBufferSize();
        long endPosition = fileChannel.position();
        long startPosition = endPosition - bufferSize;
        if (fileOffset < startPosition || fileOffset >= endPosition) {
            randomAccessFile.seek(fileOffset);
            createBufferedInputStream();
        } else {
            bufferedInputStream.setPositionInBuffer((int) (fileOffset - startPosition));
        }
    }

    @Override
    public void close() throws IOException {
        dataInputStream.close();
        randomAccessFile.close();
    }
}
