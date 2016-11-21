package ru.mipt.java2016.homework.g595.rodin.task3;

import java.io.*;
import java.nio.ByteBuffer;


class CFileHandler implements Closeable {

    private final File file;

    private OutputStream outputStream = null;

    private InputStream inputStream = null;

    private long currOffset = 0;

    public CFileHandler(String fileName) {
        this.file = new File(fileName);
    }

    public void createFile() {
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void appMode() {
        try {
            close();
            outputStream = new FileOutputStream(file.getAbsoluteFile(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists() {
        return file.exists();
    }

    private boolean checkExists() throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName());
        }
        return true;
    }

    public void flushWrite() {
        if (outputStream != null) {
            try {
                outputStream.flush();
                outputStream.close();
                outputStream = null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public long write(String text) {
        try {
            checkExists();
            checkOutput();
            byte[] bytes = ByteBuffer.allocate(4).putInt(text.length()).array();
            outputStream.write(bytes);
            outputStream.write(text.getBytes());
            return text.getBytes().length + 4;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkInput() {
        try {
            if (inputStream == null) {
                inputStream = new BufferedInputStream(new FileInputStream(file.getAbsoluteFile()));
                currOffset = 0;
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void checkOutput() {
        try {
            if (outputStream == null) {
                outputStream = new BufferedOutputStream(new FileOutputStream(file.getAbsoluteFile()));
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public String readNextToken() {
        try {
            checkExists();
            checkInput();
            if (inputStream.available() < 4) {
                closeInput();
                return null;
            }
            byte[] bytes = new byte[4];
            int read = inputStream.read(bytes, 0, 4);
            if (read < 4) {
                inputStream.close();
                throw new RuntimeException("Failed to read");
            }
            currOffset += read;
            int len = ByteBuffer.wrap(bytes).getInt();
            bytes = new byte[len];
            read = inputStream.read(bytes, 0, len);
            if (read < len) {
                inputStream.close();
                throw new RuntimeException("Failed to read");
            }
            currOffset += read;
            return new String(bytes);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public long length() {
        try (FileInputStream sin = new FileInputStream(file.getAbsoluteFile())) {
            return sin.available();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void reposition(long offset) {
        try {
            checkExists();
            if (inputStream == null || currOffset > offset) {
                if (inputStream != null) {
                    inputStream.close();
                }
                inputStream = new BufferedInputStream(new FileInputStream(file.getAbsoluteFile()));
                currOffset = 0;
            }
            while (currOffset < offset) {
                currOffset += inputStream.skip(offset - currOffset);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete() {
        file.delete();
    }

    private void closeInput() {
        try {
            if (inputStream != null) {
                inputStream.close();
                currOffset = 0;
                inputStream = null;
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void closeOutput() {
        try {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
                outputStream = null;
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void close() {
        closeInput();
        closeOutput();
    }
}