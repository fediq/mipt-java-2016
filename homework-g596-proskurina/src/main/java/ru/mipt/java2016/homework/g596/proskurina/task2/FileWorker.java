package ru.mipt.java2016.homework.g596.proskurina.task2;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Created by Lenovo on 31.10.2016.
 */
public class FileWorker implements Closeable {

    private final File file;
    private final String fileName;

    private InputStream readBuffer = null;
    private OutputStream writeBuffer = null;

    private long currentPositionInStream = 0;

    public FileWorker(String fileName) {
        this.file = new File(fileName);
        this.fileName = fileName;
    }

    public void createFile() {
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("file didn't created");
            throw new RuntimeException(e);
        }
    }

    public void rename(String newName) {
        File newFile = new File(newName);
        file.renameTo(newFile);
    }

    public void appendMode() {  
        try {
            close();
            writeBuffer = new FileOutputStream(file.getAbsoluteFile(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exist() {
        return file.exists();
    }

    private boolean innerExist() throws FileNotFoundException {
        if (!exist()) {
            throw new FileNotFoundException(fileName);
        }
        return true;
    }

    public void flushSubmit() {
        if (writeBuffer != null) {
            try {
                writeBuffer.flush();
                writeBuffer.close();
                writeBuffer = null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public long write(String str) {
        try {
            innerExist();
            if (writeBuffer == null) {
                writeBuffer = new BufferedOutputStream(new FileOutputStream(file.getAbsoluteFile()));
            }
            byte[] bytes = str.getBytes();
            byte[] bytesNumber = ByteBuffer.allocate(4).putInt(bytes.length).array();
            writeBuffer.write(bytesNumber);
            writeBuffer.write(bytes);
            return 4 + bytes.length;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String read() {
        try {
            innerExist();
            if (readBuffer == null) {
                readBuffer = new BufferedInputStream(new FileInputStream(file.getAbsoluteFile()));
                currentPositionInStream = 0;
            }
            if (readBuffer.available() < 4) {
                readBuffer.close();
                readBuffer = null;
                return null;
            }
            byte[] bytesNumberArray = new byte[4];
            int bytesNumberArraySize = readBuffer.read(bytesNumberArray, 0, 4);
            //addToCalc(bytes);
            if (bytesNumberArraySize < 4) {
                readBuffer.close();
                throw new RuntimeException("Error in reading bytes number");
            }
            currentPositionInStream += bytesNumberArraySize;
            int bytesNumber = ByteBuffer.wrap(bytesNumberArray).getInt();
            byte[] bytesArray = new byte[bytesNumber];
            int bytesArraySize = readBuffer.read(bytesArray, 0, bytesNumber);
            //addToCalc(bytes);
            if (bytesArraySize < bytesNumber) {
                readBuffer.close();
                throw new RuntimeException("Error in reading bytes");
            }
            currentPositionInStream += bytesArraySize;
            return new String(bytesArray);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long fileLength() {
        try (FileInputStream inputStream = new FileInputStream(file.getAbsoluteFile())) {
            return inputStream.available();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void goToPosition(long position) {
        try {
            innerExist();
            if (readBuffer == null || currentPositionInStream > position) {
                if (readBuffer != null) {
                    readBuffer.close();
                }
                readBuffer = new BufferedInputStream(new FileInputStream(file.getAbsoluteFile()));
                currentPositionInStream = 0;
            }
            while (currentPositionInStream < position) {
                currentPositionInStream += readBuffer.skip(position - currentPositionInStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete() {
        file.delete();
    }

    @Override
    public void close() {
        try {
            if (readBuffer != null) {
                readBuffer.close();
                currentPositionInStream = 0;
                readBuffer = null;
            }
            if (writeBuffer != null) {
                writeBuffer.flush();
                writeBuffer.close();
                writeBuffer = null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
