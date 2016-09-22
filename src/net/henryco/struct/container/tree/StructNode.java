package net.henryco.struct.container.tree;

import net.henryco.struct.container.exceptions.StructContainerException;

import java.util.*;

/**
 * @author Henry on 09/09/16.
 * Shit class, need to be reworked, actualy not used coz lang has updated
 *
 */
public class StructNode {

    public final String name;
	private static final int indent = 1;
	private static final String space = "_";

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
    public <T extends String> T getPrimitive(String name) throws StructContainerException {
        if (!this.primitives.containsKey(name)) throw new StructContainerException(name);
        return (T) this.primitives.get(name);
    }
    @SuppressWarnings("unchecked")
    public <T extends StructNode> T getStruct(String name) throws StructContainerException {
        if (!this.structures.containsKey(name)) throw new StructContainerException(name);
        return (T) this.structures.get(name);
    }
    @SuppressWarnings("unchecked")
    public <E> E get(String name) throws StructContainerException {
        if (primitives.containsKey(name)) return (E) primitives.get(name);
        if (structures.containsKey(name)) return (E) structures.get(name);
        if (this.name.equalsIgnoreCase(name)) return (E) this;
        else throw new StructContainerException(name);
    }
    @SuppressWarnings("unchecked")
    public <T extends StructNode> T getParent() {
        return (T) parent;
    }

    public boolean contains(String name) {
		return structures.containsKey(name) || primitives.containsKey(name);
	}
	public boolean containsStruct(String name) {
		return structures.containsKey(name);
	}
	public boolean containsPrimitive(String name) {
		return primitives.containsKey(name);
	}

	public String[] getPrimitiveChild() {
		return primitives.keySet().toArray(new String[primitives.keySet().size()]);
	}
	public String[] getStructChild() {
		return structures.keySet().toArray(new String[structures.keySet().size()]);
	}

	public String[] getChild() {
		List<String> primList = new ArrayList<>();
		Arrays.stream(this.getPrimitiveChild()).forEach(primList::add);
		Arrays.stream(this.getStructChild()).forEach(primList::add);
		return primList.toArray(new String[primList.size()]);
	}

    public String printTreeView(int increment, String space) {

		String spaces = "";
		for (int i = 0; i < increment; i++) spaces = spaces +""+space;
		String sName = spaces + this.name;
		String[] primKeySet = primitives.keySet().toArray(new String[primitives.size()]);
		for (String key : primKeySet)
			sName += "\n"+spaces+""+space+key+": "+this.getPrimitive(key);
		String[] structKeySet = structures.keySet().toArray(new String[structures.size()]);
		for (String key : structKeySet) {
			StructNode child = this.getStruct(key);
			sName += "\n" + child.printTreeView(increment + indent, space);
		}
		return sName;
	}

	@Override
	public String toString(){
		return printTreeView(0, space);
	}

}
