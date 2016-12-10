package ru.mipt.java2016.homework.g594.kozlov.task2;

import java.io.*;
import java.nio.ByteBuffer;

import static java.awt.SystemColor.text;

/**
 * Created by Anatoly on 26.10.2016.
 */
public class FileWorker {

    private final File file;

    private final String fileName;

    private BufferedOutputStream buffWr = null;

    private BufferedInputStream buffRd = null;

    public FileWorker(String fileName) {
        this.file = new File(fileName);
        this.fileName = fileName;
    }

    public void createFile() {
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("failed to create file");
            throw new RuntimeException(e);
        }
    }

    public boolean exists() throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName());
        }
        return true;
    }

    public void bufferedWriteSubmit() {
        if (buffWr != null) {
            try {
                buffWr.flush();
                buffWr.close();
                buffWr = null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public long bufferedWrite(String text) {
        try {
            exists();
            if (buffWr == null) {
                buffWr = new BufferedOutputStream(new FileOutputStream(file.getAbsoluteFile()));
            }
            byte[] bytes = ByteBuffer.allocate(4).putInt(text.length()).array();
            buffWr.write(bytes);
            buffWr.write(text.getBytes());
            return text.getBytes().length + 4;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void bufferedWriteOffset(long offset) {
        try {
            exists();
            if (buffWr == null) {
                buffWr = new BufferedOutputStream(new FileOutputStream(file.getAbsoluteFile()));
            }
            byte[] bytes = ByteBuffer.allocate(8).putLong(offset).array();
            buffWr.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void append(String str) {
        try (OutputStream outputStr = new FileOutputStream(file.getAbsoluteFile(), true)) {
            byte[] bytes = ByteBuffer.allocate(4).putInt(str.length()).array();
            outputStr.write(bytes);
            outputStr.write(str.getBytes());
            outputStr.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readNextToken() {
        try {
            exists();
            if (buffRd == null) {
                buffRd = new BufferedInputStream(new FileInputStream(file.getAbsoluteFile()));
            }
            if (buffRd.available() < 4) {
                buffRd.close();
                buffRd = null;
                return null;
            }
            byte[] bytes = new byte[4];
            int read = buffRd.read(bytes, 0, 4);
            if (read < 4) {
                throw new RuntimeException("Reading failure");
            }
            int len = ByteBuffer.wrap(bytes).getInt();
            bytes = new byte[len];
            read = buffRd.read(bytes, 0, len);
            if (read < len) {
                throw new RuntimeException("Reading failure");
            }
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long readLong() {
        try {
            exists();
            if (buffRd == null) {
                buffRd = new BufferedInputStream(new FileInputStream(file.getAbsoluteFile()));
            }
            byte[] bytes = new byte[8];
            int read = buffRd.read(bytes, 0, 8);
            if (read < 8) {
                throw new RuntimeException("Reading failure");
            }
            long result = ByteBuffer.wrap(bytes).getLong();
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void refresh() {
        try {
            if (buffRd != null) {
                buffRd.close();
                buffRd = null;
            }
            if (buffWr != null) {
                buffWr.flush();
                buffWr.close();
                buffWr = null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readFromOffset(long offset) {
        try (FileInputStream buffReader = new FileInputStream(file.getAbsoluteFile())) {
            exists();
            buffReader.skip(offset);
            byte[] bytes = new byte[4];
            int read = buffReader.read(bytes, 0, 4);
            if (read < 4) {
                throw new RuntimeException("Reading failure " + read);
            }
            int len = ByteBuffer.wrap(bytes).getInt();
            bytes = new byte[len];
            read = buffReader.read(bytes, 0, len);
            if (read < len) {
                throw new RuntimeException("Reading failure");
            }
            buffReader.close();
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
