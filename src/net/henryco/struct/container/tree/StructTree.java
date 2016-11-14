package net.henryco.struct.container.tree;


import net.henryco.struct.Struct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Henry on 13/09/16.
 */
public class StructTree {

    private static final String main = "main";

    private Map<String, Object> storage;
    public final StructNode mainNode;

    {
        storage = new HashMap<>();
        mainNode = new StructNode(main, "absolute");
        storage.put(main, mainNode);
    }
    public StructTree() {}
    public StructTree(List<String[]>[] data, String name) {
        loadContainer(data, name);
    }
	public StructTree(String name) {
		loadContainer(Struct.in.readStructData(name), name);
	}

    public StructTree loadContainer(List<String[]>[] data, String dirName) {

        long time0 = System.nanoTime();
        List<String[]> dataList = data[1];
        StructNode actual = mainNode;

        for (String[] line : dataList) {
            if (line[line.length - 1].endsWith("{")) {
                StructNode node;
                if (!actual.contains(line[line.length - 2])) {
                    node = new StructNode(actual, line[line.length - 2], dirName);
                    actual.addStructure(node);
                    storage.put(node.name, node);
                }
                else node = actual.getStruct(line[line.length - 2]);
                actual = node;
            }
            else {
                if (line[0].endsWith("}")) actual = actual.getParent();
				else if (line[0].startsWith("*")) {
					//TODO POINTERS
					System.out.print("pointer:["+line[0]+"]  link:[...");
					for (int k = 1; k < line.length - 1; k++) System.out.print(" ->"+line[k]);
					System.out.print("]  target:["+line[line.length-1]+"]\n");
				}
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


    @Override
    public String toString(){
		return "\n:: TREE VIEW ::\n"+this.mainNode.toString()+"\n:: END TREE VIEW ::\n";
    }

}
