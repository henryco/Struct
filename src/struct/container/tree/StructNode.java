package struct.container.tree;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Henry on 09/09/16.
 * Shit class, need to be reworked, actualy not used coz lang has updated
 *
 */
public class StructNode {

    public final String name;

    private StructNode parent = null;
    private Map<String, Object> primitives = new HashMap<>();
    private Map<String, Object> structures = new HashMap<>();


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
    public StructNode addPrimitive(String name, String[] value) {
        this.primitives.put(name, value);
        return this;
    }

    public StructNode addStructure(StructNode struct){
        this.structures.put(struct.name, struct);
        return this;
    }

    public StructNode addStructure(StructNode[] struct){
        Arrays.stream(struct).forEach(st->structures.put(st.name, st));
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends String> T getPrimitive(String name) {
        return (T) this.primitives.get(name);
    }
    @SuppressWarnings("unchecked")
    public <T extends StructNode> T getStruct(String name){
        return (T) this.structures.get(name);
    }
    @SuppressWarnings("unchecked")
    public <E> E get(String name) {
        if (primitives.containsKey(name)) return (E) primitives.get(name);
        if (structures.containsKey(name)) return (E) structures.get(name);
        if (this.name.equalsIgnoreCase(name)) return (E) this;
        else return (E) new Integer(-1);
    }
    @SuppressWarnings("unchecked")
    public <T extends StructNode> T getParent() {
        return (T) parent;
    }
}
