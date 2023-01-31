package screens;

import java.util.HashMap;

public class ScreenOpenArgs {

    private HashMap<String, Object> args;

    public ScreenOpenArgs() {
        args = new HashMap<>();
    }

    public void add(String name, Object value) {
        args.put(name, value);
    }
    public Object get(String name) {
        return args.get(name);
    }
    public boolean has(String name) {
        return args.containsKey(name);
    }
}
