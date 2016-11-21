package ru.mipt.java2016.homework.g594.borodin.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.borodin.task3.SerializationStrategies.SerializationStrategy;

/**
 * Created by Maxim on 10/28/2016.
 */
public class KVStorage<K, V> implements KeyValueStorage<K, V> {

	private static final String VALIDATION = "We gonna celebrate. https://youtu.be/-ao7LpSIL7A?t=58s";
	private static final int MAX_SIZE_OF_CACHE = 500;

	private boolean isOpen;

	private HashMap<K, V> cacheStorage;
	private HashMap<K, KeyPosition> keys;
	private HashMap<K, V> toWriteStorage;
	private ArrayList<RandomAccessFile> files;
	private String keyFileName;
	private String directory;
	private File keyFile;
	private SerializationStrategy keyStrategy;
	private SerializationStrategy valueStrategy;

	public KVStorage(String directory, SerializationStrategy<K> keyStrategy,
	                 SerializationStrategy<V> valueStrategy) {
		this.directory = directory;
		this.keyStrategy = keyStrategy;
		this.valueStrategy = valueStrategy;
		isOpen = true;
		cacheStorage = new HashMap<>();
		keys = new HashMap<>();
		toWriteStorage = new HashMap<>();
		files = new ArrayList<>();
		keyFileName = directory + File.separator + "keysFile.txt";
		keyFile = new File(keyFileName);

		if (keyFile.exists()) {
			try {
				DataInputStream dataInputStream = new DataInputStream(new FileInputStream(keyFile));
				String firstFileString = dataInputStream.readUTF();
				if (!firstFileString.equals(VALIDATION)) {
					throw new RuntimeException("File storage is invalid");
				}
				int filesCount = dataInputStream.readInt();
				for (int i = 0; i < filesCount; ++i) {
					File newFile = new File(directory + File.separator + Integer.toString(i) + ".txt");
					if (!newFile.exists()) {
						throw new RuntimeException("Can't find file\n");
					}
					files.add(new RandomAccessFile(newFile, "rw"));
				}
				int keysCount = dataInputStream.readInt();
				for (int i = 0; i < keysCount; ++i) {
					K newKey = keyStrategy.deserialize(dataInputStream);
					keys.put(newKey, new KeyPosition(dataInputStream.readLong(), dataInputStream.readLong()));
				}
			} catch (FileNotFoundException e) {
				throw new RuntimeException("File not found");
			} catch (IOException e) {
				throw new RuntimeException("Can't read from file");
			}
		} else {
			try {
				keyFile.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException("Can't create file for storage");
			}
		}
	}

	/**
	 * Возвращает значение для данного ключа, если оно есть в хранилище.
	 * Иначе возвращает null.
	 * Смотрит в map ключей, есть ли такой ключ вообще
	 * Если есть, то проверяет на наличие в кэше оперативной памяти
	 * Если там тоже нет, то может его как раз недавно нужно было записать(смотрит в
	 * toWriteStorage)
	 * Иначе ищет в файле
	 */
	@Override
	public synchronized V read(K key) {
		if (!isOpen) {
			throw new RuntimeException("Storage is closed");
		}
		if (!keys.containsKey(key)) {
			return null;
		}
		if (cacheStorage.containsKey(key)) {
			return cacheStorage.get(key);
		}
		if (toWriteStorage.containsKey(key)) {
			return toWriteStorage.get(key);
		}
		KeyPosition keyPosition = keys.get(key);
		RandomAccessFile randomAccessFile = files.get((int) keyPosition.getFileNumber());
		try {
			randomAccessFile.seek(keyPosition.getPositionInFile());
			V value = (V) valueStrategy.deserialize(randomAccessFile);
			if (cacheStorage.size() >= MAX_SIZE_OF_CACHE) {
				cacheStorage.clear();
			}
			cacheStorage.put(key, value);
			return value;
		} catch (IOException exception) {
			throw new RuntimeException("Can't seek in file\n");
		}
	}

	/**
	 * Возвращает true, если данный ключ есть в хранилище
	 */
	@Override
	public synchronized boolean exists(K key) {
		if (!isOpen) {
			throw new RuntimeException("Storage is closed");
		}
		return keys.containsKey(key);
	}

	/**
	 * Записывает в хранилище пару ключ-значение.
	 * Если есть место в локальном буфере, пишем в него, и добавляем
	 * в keys ключ и его неинициализированное значение местоположения
	 * Инициализируем при записи на диск
	 */
	@Override
	public synchronized void write(K key, V value) {
		if (!isOpen) {
			throw new RuntimeException("Storage is closed");
		}
		if (toWriteStorage.size() < MAX_SIZE_OF_CACHE) {
			toWriteStorage.put(key, value);
			keys.put(key, new KeyPosition(-1, -1));
		} else {
			writeTemporaryStorage();
		}
	}

	/**
	 * Удаляет пару ключ-значение из хранилища.
	 */
	@Override
	public synchronized void delete(K key) {
		if (!isOpen) {
			throw new RuntimeException("Storage is closed");
		}
		if (keys.containsKey(key)) {
			cacheStorage.remove(key);
			toWriteStorage.remove(key);
			keys.remove(key);
		}
	}

	/**
	 * Читает все ключи в хранилище.
	 * <p>
	 * Итератор должен бросать {@link java.util.ConcurrentModificationException},
	 * если данные в хранилище были изменены в процессе итерирования.
	 */
	@Override
	public synchronized Iterator<K> readKeys() {
		if (!isOpen) {
			throw new RuntimeException("Storage is closed");
		}
		return cacheStorage.keySet().iterator();
	}

	/**
	 * Возвращает число ключей, которые сейчас в хранилище.
	 */
	@Override
	public int size() {
		if (!isOpen) {
			throw new RuntimeException("Storage is closed");
		}
		return keys.size();
	}

	/**
	 * Закрывает хранилище, записывая перед этим данные из storage в файл
	 */

	@Override
	public synchronized void close() throws IOException {
		if (!isOpen) {
			throw new RuntimeException("Storage is closed");
		}
		saveToStorage();
		isOpen = false;
	}

	/**
	 * Создаём поток вывода, переписываем из toWriteStorage информацию в файл,
	 * пишем УНИКАЛЬНУЮ строку валидации (самая надёжная проверка) в keyFile
	 * После чего пробегаемся по keys, сериализуем key, его KeyPosition
	 */

	public synchronized void saveToStorage() {
		try {
			DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(keyFileName));
			dataOutputStream.writeUTF(VALIDATION);
			dataOutputStream.writeInt(files.size());
			dataOutputStream.writeInt(this.size());
			for (Map.Entry<K, KeyPosition> entry : keys.entrySet()) {
				keyStrategy.serialize(entry.getKey(), dataOutputStream);
				dataOutputStream.writeLong(entry.getValue().getFileNumber());
				dataOutputStream.writeLong(entry.getValue().getPositionInFile());
			}
			dataOutputStream.close();
		} catch (IOException e) {
			throw new RuntimeException("Can't write to storage");
		}
	}

	public synchronized void writeTemporaryStorage() {
		String newFileName = directory + File.separator + Integer.toString(files.size()) + ".txt";
		File newFile = new File(newFileName);
		if (newFile.exists()) {
			throw new RuntimeException("Unexpected file\n");
		}
		try {
			newFile.createNewFile();
			RandomAccessFile newRAFile = new RandomAccessFile(newFile, "rw");
			newRAFile.seek(0);
			DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(newFileName));
				for (Map.Entry<K, V> entry : toWriteStorage.entrySet()) {
				KeyPosition keyPosition = new KeyPosition(files.size(), newRAFile.getFilePointer());
				keys.remove(entry.getKey());
				keys.put(entry.getKey(), keyPosition);
					valueStrategy.serialize(entry.getValue(), dataOutputStream);
			}
			dataOutputStream.close();
			toWriteStorage.clear();
			files.add(newRAFile);
		} catch (IOException e) {
			throw new RuntimeException("Can't create new file or get access to it\n");
		}
	}
}
