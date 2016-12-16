package ru.mipt.java2016.homework.g594.kozlov.task2;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.zip.Adler32;

/**
 * Created by Anatoly on 26.10.2016.
 */
public class FileWorker implements Closeable {

    private final File file;
    private final String fileName;
    private OutputStream buffWr = null;
    private InputStream buffRd = null;
    private long currOffset = 0;
    private boolean mustCalc = false;
    private Adler32 adl = new Adler32();

    public FileWorker(String fileName, boolean mc) {
        this.file = new File(fileName);
        mustCalc = mc;
        this.fileName = fileName;
        adl.reset();
    }

    public void createFile() {
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("failed to create file");
            throw new RuntimeException(e);
        }
    }

    public void rename(String str) {
        File newf = new File(str);
        file.renameTo(newf);
    }

    public long getCheckSum() {
        if (!mustCalc) {
            return 0;
        }
        return adl.getValue();
    }

    public void appMode() {
        try {
            close();
            buffWr = new FileOutputStream(file.getAbsoluteFile(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists() {
        return file.exists();
    }

    private boolean innerExists() throws FileNotFoundException {
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

    private void addToCalc(byte[] bytes) {
        if (mustCalc) {
            adl.update(bytes);
        }
    }

    public long bufferedWrite(String text) {
        try {
            innerExists();
            if (buffWr == null) {
                buffWr = new BufferedOutputStream(new FileOutputStream(file.getAbsoluteFile()));
            }
            byte[] obj = text.getBytes();
            byte[] bytes = ByteBuffer.allocate(4).putInt(obj.length).array();
            buffWr.write(bytes);
            addToCalc(bytes);
            addToCalc(obj);
            buffWr.write(obj);
            return obj.length + 4;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void bufferedWriteOffset(long offset) {
        try {
            innerExists();
            if (buffWr == null) {
                buffWr = new BufferedOutputStream(new FileOutputStream(file.getAbsoluteFile()));
            }
            byte[] bytes = ByteBuffer.allocate(8).putLong(offset).array();
            addToCalc(bytes);
            buffWr.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long append(String str) {
        try (OutputStream outputStr = new FileOutputStream(file.getAbsoluteFile(), true)) {
            byte[] bytes = ByteBuffer.allocate(4).putInt(str.getBytes().length).array();
            outputStr.write(bytes);
            outputStr.write(str.getBytes());
            addToCalc(bytes);
            addToCalc(str.getBytes());
            outputStr.close();
            return str.getBytes().length + 4;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readNextToken() {
        try {
            innerExists();
            if (buffRd == null) {
                buffRd = new BufferedInputStream(new FileInputStream(file.getAbsoluteFile()));
                currOffset = 0;
            }
            if (buffRd.available() < 4) {
                buffRd.close();
                buffRd = null;
                return null;
            }
            byte[] bytes = new byte[4];
            int read = buffRd.read(bytes, 0, 4);
            addToCalc(bytes);
            if (read < 4) {
                buffRd.close();
                throw new RuntimeException("Reading failure");
            }
            currOffset += read;
            int len = ByteBuffer.wrap(bytes).getInt();
            bytes = new byte[len];
            read = buffRd.read(bytes, 0, len);
            addToCalc(bytes);
            if (read < len) {
                buffRd.close();
                throw new RuntimeException("Reading failure");
            }
            currOffset += read;
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long fileLen() {
        try (FileInputStream sin = new FileInputStream(file.getAbsoluteFile())) {
            return sin.available();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long readLong() {
        try {
            innerExists();
            if (buffRd == null) {
                buffRd = new BufferedInputStream(new FileInputStream(file.getAbsoluteFile()));
                currOffset = 0;
            }
            byte[] bytes = new byte[8];
            int read = buffRd.read(bytes, 0, 8);
            if (read < 8) {
                throw new RuntimeException("Reading failure");
            }
            addToCalc(bytes);
            currOffset += 8;
            return ByteBuffer.wrap(bytes).getLong();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void moveToOffset(long offset) {
        try {
            innerExists();
            if (buffRd == null || currOffset > offset) {
                if (buffRd != null) {
                    buffRd.close();
                }
                buffRd = new BufferedInputStream(new FileInputStream(file.getAbsoluteFile()));
                currOffset = 0;
            }
            while (currOffset < offset) {
                currOffset += buffRd.skip(offset - currOffset);
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
            if (buffRd != null) {
                buffRd.close();
                currOffset = 0;
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
}
