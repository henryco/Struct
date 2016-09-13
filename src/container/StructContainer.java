package container;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Henry on 13/09/16.
 */
public class StructContainer {

    public static final String main = "main";

    private Map<String, Object> storage;
    public StructNode mainNode;

    {
        storage = new HashMap<>();
        mainNode = new StructNode(main);
        storage.put(main, mainNode);
    }

}
