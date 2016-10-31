package ru.mipt.java2016.homework.g594.stepanov.task2;

import javafx.util.Pair;

import java.io.*;

public abstract class ObjectSerializator<K, V> {

    abstract void write(K key, V value) throws IOException;

    abstract Pair<K, V> read() throws IOException;

    protected String start = "[";
    protected String numerator = ",";
    protected String separator = ":";
    protected String finish = "]";
    protected BufferedReader inputStream;
    protected PrintWriter outputStream;

    public ObjectSerializator(String directory) throws IOException {
        path = directory;
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdir();
            dir.createNewFile();
        }
        StringBuilder builder = new StringBuilder();
        builder.append(path);
        builder.append("/Storage.db");
        storageName = builder.toString();
        builder = new StringBuilder();
        builder.append(path);
        builder.append("/HashCode.txt");
        hashName = builder.toString();
        if ((new File(storageName).exists())) {
            if (!validate()) {
                validState = false;
            }
        } else {
            File tmp = new File(storageName);
            tmp.createNewFile();
        }
    }


    boolean validate() throws IOException {
        inputStream = new BufferedReader(new FileReader(storageName));
        while (true) {
            try {
                Pair<K, V> p = read();
                currentHash += p.hashCode();
            } catch (IOException e) {
                if (e.getMessage().equals("File end")) {
                    break;
                } else {
                    validState = false;
                    inputStream.close();
                    return false;
                }
            }
        }
        BufferedReader hashReader = new BufferedReader(new FileReader(hashName));
        String writtenHash = hashReader.readLine();
        if (Integer.parseInt(writtenHash) != currentHash) {
            validState = false;
        }
        hashReader.close();
        inputStream.close();
        return validState;
    }

    void terminate() throws IOException {
        if (writing) {
            outputStream.close();
            writing = false;
        }
        if (reading) {
            try {
                inputStream.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            reading = false;
        }
    }

    void getReadyToRead() {
        try {
            terminate();
            inputStream = new BufferedReader(new FileReader(storageName));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        reading = true;
    }

    void getReadyToWrite() {
        try {
            terminate();
            outputStream = new PrintWriter(new File(storageName));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        writing = true;
    }

    void writeHash() throws IOException {
        terminate();
        outputStream = new PrintWriter(new File(hashName));
        outputStream.print(currentHash);
        outputStream.close();
    }

    protected Integer currentHash = 0;
    protected boolean validState = true;
    private String path;
    private String storageName;
    private String hashName;

    private boolean writing = false;
    private boolean reading = false;

    public void setCurrentHashToNull() {
        currentHash = 0;
    }

}

