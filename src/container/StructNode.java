package container;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Henry on 09/09/16.
 */
public class StructNode {

    public final String name;
    public StructNode parent = null;
    private Map<String, Object> primitives = new HashMap<>();
    private Map<String, StructNode> structures = new HashMap<>();

    public StructNode(String name) {
        this.parent = this;
        this.name = name;
    }
    public StructNode(StructNode parent, String name) {
        this.parent = parent;
        this.name = name;
    }


    public StructNode addPrimitive(String name, String value) {
        this.primitives.put(name, value);
        return this;
    }
    public StructNode addStructure(StructNode struct){
        return this.structures.put(struct.name, struct);
    }

    public <T> T getPrimitive(String name) {
        return (T) this.primitives.get(name);
    }
    public StructNode getStruct(String name){
        return this.structures.get(name);
    }


}
