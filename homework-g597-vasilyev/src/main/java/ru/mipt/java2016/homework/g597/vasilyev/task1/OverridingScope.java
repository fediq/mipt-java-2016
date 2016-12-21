package ru.mipt.java2016.homework.g597.vasilyev.task1;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mizabrik on 21.12.16.
 */
public class OverridingScope implements Scope {
    private Scope origin;
    private Map<String, Command> overrides = new HashMap<>();

    public OverridingScope(Scope origin) {
        this.origin = origin;
    }

    public void addOverride(String name, Command command) {
        overrides.put(name, command);
    }

    @Override
    public Command getCommand(String name) {
        if (overrides.containsKey(name)) {
            return overrides.get(name);
        } else {
            return origin.getCommand(name);
        }
    }

    @Override
    public boolean hasCommand(String name) {
        return overrides.containsKey(name) || origin.hasCommand(name);
    }
}
