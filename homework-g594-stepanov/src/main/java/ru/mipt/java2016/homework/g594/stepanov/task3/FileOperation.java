package ru.mipt.java2016.homework.g594.stepanov.task3;

import java.io.*;

public abstract class FileOperation<K, V> {

    private PrintWriter outputStream;
    private BufferedReader inputStream;
    protected ObjectSerialisator<K> keys;
    protected ObjectSerialisator<V> values;
    private Long currentInputOffset;
    private Long currentOutputOffset;
    private File file;

    public FileOperation(String fileName) {
        file = new File(fileName);
        try {
            file.createNewFile();
            inputStream = new BufferedReader(new FileReader(file));
            outputStream = new PrintWriter(new FileOutputStream(file, true));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        currentInputOffset = new Long(0);
        currentOutputOffset = file.length();
    }

    public K readCurrentKey() {
        String s = null;
        try {
            s = inputStream.readLine();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        currentInputOffset += s.length() + 1;
        return keys.toObject(s);
    }

    public V readCurrentValue() {
        String s = null;
        try {
            s = inputStream.readLine();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        currentInputOffset += s.length() + 1;
        return values.toObject(s);
    }

    private void goToOffset(Long offset) {
        if (offset >= currentInputOffset) {
            try {
                inputStream.skip(offset - currentInputOffset);
                currentInputOffset = offset;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            try {
                inputStream = new BufferedReader(new FileReader(file));
                inputStream.skip(offset);
                currentInputOffset = offset;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public K readCertainKey(Long offset) {
        goToOffset(offset);
        return readCurrentKey();
    }

    public V readCertainValue(Long offset) {
        goToOffset(offset);
        return readCurrentValue();
    }

    public Long appendKey(K key) { // returns offset
        String s = keys.toString(key);
        Long ans = currentOutputOffset;
        outputStream.append(s);
        currentOutputOffset += s.length();
        return ans;
    }

    public Long appendValue(V value) {
        String s = values.toString(value);
        Long ans = currentOutputOffset;
        outputStream.append(s);
        currentOutputOffset += s.length();
        return ans;
    }

    public void close() {
        try {
            flush();
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void setToStart() {
        try {
            inputStream.close();
            outputStream.close();
            inputStream = new BufferedReader(new FileReader(file));
            outputStream = new PrintWriter(new FileOutputStream(file, true));
            currentInputOffset = new Long(0);
            currentOutputOffset = file.length();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean hasNext() {
        return !currentInputOffset.equals(currentOutputOffset);
    }

    public void flush() {
        outputStream.flush();
    }

    public Long getCurrentInputOffset() {
        return currentInputOffset;
    }

}
