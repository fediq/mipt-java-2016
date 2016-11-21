package ru.mipt.java2016.homework.g594.borodin.task3;

/**
 * Created by Maxim on 11/21/2016.
 */
public class KeyPosition {
	private long fileNumber;
	private long positionInFile;

	KeyPosition (long fileNumber, long positionInFile) {
		this.fileNumber = fileNumber;
		this.positionInFile = positionInFile;
	}

	public long getFileNumber() {
		return fileNumber;
	}

	public long getPositionInFile() {
		return positionInFile;
	}
}
