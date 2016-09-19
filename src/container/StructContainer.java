package container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Henry on 13/09/16.
 */
public class StructContainer {

    public static final String main = "main";

    private Map<String, Object> storage;
    public final StructNode mainNode;

    {
        storage = new HashMap<>();
        mainNode = new StructNode(main);
        storage.put(main, mainNode);
    }
    public StructContainer() {}
    public StructContainer(List<String[]>[] data) {
        loadContainer(data);

    }

    public StructContainer loadContainer(List<String[]>[] data) {

        long time0 = System.nanoTime();
        List<String[]> dataList = data[1];
        StructNode actual = mainNode;

        for (String[] line : dataList) {
            if (line[line.length - 1].endsWith("{")) {
                StructNode node = new StructNode(actual, line[line.length - 2]);
                actual.addStructure(node);
                storage.put(node.name, node);
                actual = node;
            }
            else {
                if (line[0].endsWith("}")) actual = actual.parent;
                else {
                    String[] primitive = new String[]{line[line.length - 2], line[line.length - 1]};
                    actual.addPrimitive(primitive[0], primitive[1]);
                    storage.put(primitive[0], primitive[1]);
                }
            }
        }
        long time1 = System.nanoTime() - time0;
        String out = "Loaded STRUCT_CONTAINER in: " + time1 + " ns";
        printUnderlined(out, "#");
        return this;
    }

    private static void printUnderlined(String msg, String sym) {
        System.out.println("");
        System.out.println(msg);
        for (int i = 0; i < msg.length(); i++) System.out.print(sym);
        System.out.println("");
    }

    public Map<String, Object> getStorage() {
        return this.storage;
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String name) {
        return (T) storage.get(name);
    }

}
