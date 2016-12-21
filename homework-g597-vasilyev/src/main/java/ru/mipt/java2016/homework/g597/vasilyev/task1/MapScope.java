package ru.mipt.java2016.homework.g597.vasilyev.task1;

import java.util.Map;

/**
 * Created by mizabrik on 21.12.16.
 */
public class MapScope implements Scope {
    private Map<String, Command> map;

    public MapScope(Map<String, Command> map) {
        this.map = map;
    }

    @Override
    public Command getCommand(String name) {
        return map.get(name);
    }

    @Override
    public boolean hasCommand(String name) {
        return map.containsKey(name);
    }
}
