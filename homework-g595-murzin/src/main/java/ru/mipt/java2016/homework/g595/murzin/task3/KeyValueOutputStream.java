package ru.mipt.java2016.homework.g595.murzin.task3;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Map;

/**
 * Created by dima on 06.11.16.
 */
public class KeyValueOutputStream<K, V> extends DataOutputStream {
    private FileChannel fileChannel;
    private HackedBufferedOutputStream hackedBufferedOutputStream;
    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;

    public KeyValueOutputStream(File file,
                                SerializationStrategy<K> keySerializationStrategy,
                                SerializationStrategy<V> valueSerializationStrategy,
                                int numberEntries) throws IOException {
        super(new HackedBufferedOutputStream(new FileOutputStream(file)));
        hackedBufferedOutputStream = (HackedBufferedOutputStream) out;
        FileOutputStream fileOutputStream = (FileOutputStream) hackedBufferedOutputStream.getOut();
        fileChannel = fileOutputStream.getChannel();
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;
        writeInt(numberEntries);
    }

    public long writeEntry(Map.Entry<K, V> entry) throws IOException {
        keySerializationStrategy.serializeToStream(entry.getKey(), this);
        long position = getPosition();
        valueSerializationStrategy.serializeToStream(entry.getValue(), this);
        return position;
    }

    private long getPosition() throws IOException {
        // fileChannel                  $..........[.........#........)...........^
        // buffer                                  [.........#........)
        // buffer[positionInBuffer]                          #
        // bufferSize                              <------------------>
        // positionInBuffer                        <--------->
        // startPosition                <---------->

        long positionInBuffer = hackedBufferedOutputStream.getPositionInBuffer();
        long startPosition = fileChannel.position();
        return startPosition + positionInBuffer;
    }
}
