package ru.mipt.java2016.homework.g597.sigareva.task2;

import javafx.util.Pair;

import java.io.*;

abstract class ObjectSerializer<K, V> {

    abstract void write(K key, V value);

    protected BufferedReader inputStream;
    protected PrintWriter outputStream;

    private String myFilePath;

    private boolean openInputStream = false;
    private boolean openOutputStream = false;

    protected ObjectSerializer(String newPath) {
        myFilePath =  newPath + File.separator + "Lenin";
        File file = new File(myFilePath);

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("Something is bad\n");
        }
    }

    protected String lastRead;

    public boolean canRead() {
        try {
            lastRead = inputStream.readLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return lastRead != null;
    }

    protected abstract Pair<K, V> convert();

    public void checkBeforeRead() throws IOException {
        if (openInputStream) {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new IllegalStateException("Something is bad\n");
            }
        }
        if (openOutputStream) {
            outputStream.close();
        }
        try {
            inputStream = new BufferedReader(new FileReader(myFilePath));
            openInputStream = true;
        } catch (IOException e) {
            throw new IllegalStateException("Something is bad\n");
        }
    }

    public void checkBeforeWrite() throws IOException {
        if (openInputStream) {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new IllegalStateException("Something is bad\n");
            }
        }
        if (openOutputStream) {
            outputStream.close();
        }
        try {
            outputStream = new PrintWriter(new FileWriter(myFilePath));
            openOutputStream = true;
        } catch (IOException e) {
            throw new IllegalStateException("Something is bad\n");
        }
    }
}
