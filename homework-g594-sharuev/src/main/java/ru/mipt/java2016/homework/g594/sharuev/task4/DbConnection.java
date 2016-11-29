package ru.mipt.java2016.homework.g594.sharuev.task4;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class DbConnection {
    private ConnectionSource source;

    DbConnection() {
        source = new JdbcConnectionSource();
    }
}
