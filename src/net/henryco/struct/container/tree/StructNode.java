package net.henryco.struct.container.tree;

import net.henryco.struct.Struct;
import net.henryco.struct.container.StructContainer;
import net.henryco.struct.container.exceptions.StructContainerException;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Henry on 09/09/16.
 *         Shit class, need to be reworked, actualy not used coz lang has updated
 */
public class StructNode {

	public final String name;
	public final String dirName;
	private static final int indent = 1;
	private static final String space = "_";
	public static String fileSeq = "file";
	public static String pathSeq = "path";
	public static String[] extSeq = {"ext", "external"};

	private StructNode parent = null;
	private Map<String, Object> primitives = new HashMap<>();
	private Map<String, Object> structures = new HashMap<>();
	private Map<String, Object> pointers = new HashMap<>();


	public StructNode(String name, String dirName) {
		this.parent = this;
		this.name = name;
		this.dirName = dirName;
	}

	public StructNode(StructNode parent, String name, String dirName) {
		this.parent = parent;
		this.name = name;
		this.dirName = dirName;
	}

	public StructNode almostCopy(StructNode otherParent, String... name_dirName) {
		String newname = this.name;
		String newDir = this.dirName;
		if (name_dirName.length == 1) newname = name_dirName[0];
		if (name_dirName.length >= 2) newDir = name_dirName[1];

		StructNode newNode = new StructNode(otherParent, newname, newDir);
		newNode.primitives = new HashMap<>(this.primitives);
		newNode.structures = new HashMap<>(this.structures);
		newNode.pointers = new HashMap<>(this.pointers);

		return newNode;
	}

	public StructNode addPointer(String name, Object object) {
		String forPut = "&"+name.substring(1);
		pointers.put(forPut, object);
		if (Struct.log_loading) {
			System.out.println("NODE: " +this.getParent().name+"->"+this.name);
			System.out.println("POINTER NAME: "+forPut);
			if (object instanceof String) System.out.println("POINTER PRIM: "+(String)object);
			else System.out.println("POINTER STRUCT: "+((StructNode)object).name);
		}
		return this;
	}

	public StructNode addPrimitive(String name, String value) {
		this.primitives.put(name, value);
		return this;
	}

	public StructNode addPrimitive(String name, String[] value) {
		this.primitives.put(name, value);
		return this;
	}

	public StructNode addStructure(StructNode struct) {
		this.structures.put(struct.name, struct);
		return this;
	}

	public StructNode addStructure(StructNode[] struct) {
		Arrays.stream(struct).forEach(st -> structures.put(st.name, st));
		return this;
	}

	public int getInt(int i, String... name) {
		try {
			i = Integer.parseInt(getPrimitive(name));
		} catch (StructContainerException e) {
		}
		return i;
	}
	public int getIntPath(int i, String name, String... path) {
		try {
			i = Integer.parseInt(getPathPrimitive(name, path));
		} catch (StructContainerException e) {
		}
		return i;
	}

	public float getFloat(float f, String... name) {
		try {
			f = Float.parseFloat(getPrimitive(name));
		} catch (StructContainerException e) {
		}
		return f;
	}
	public float getFloatPath(float f, String name, String... path) {
		try {
			f = Float.parseFloat(getPathPrimitive(name, path));
		} catch (StructContainerException e) {
		}
		return f;
	}

	public boolean getBool(boolean b, String... name) {
		try {
			b = Boolean.parseBoolean(getPrimitive(name));
		} catch (StructContainerException e) {
		}
		return b;
	}
	public boolean getBoolPath(boolean b, String name, String... path) {
		try {
			b = Boolean.parseBoolean(getPathPrimitive(name, path));
		} catch (StructContainerException e) {
		}
		return b;
	}

	public String getString(String s, String... name) {
		try {
			s = getPrimitive(name);
		} catch (StructContainerException e) {
		}
		return s;
	}
	public String getStringPath(String s, String name, String... path) {
		try {
			s = getPathPrimitive(name);
		} catch (StructContainerException e) {
		}
		return s;
	}

	public StructNode getStructSafe(int index) {
		return getStructSafe(Integer.toString(index));
	}
	public StructNode getStructSafe(String... name) {
		StructNode node = null;
		try {
			node = getStruct(name);
			if (node.containsStruct(extSeq[0]) || node.containsStruct(extSeq[1]))
				node = StructContainer.loadFromFile(node.getStruct(extSeq[0], extSeq[1]), dirName.substring(0, dirName.lastIndexOf("/") + 1), node.name, fileSeq, pathSeq,
						dirName.substring(0, dirName.lastIndexOf("/") + 1), node.name);
		} catch (StructContainerException e) {
		}
		return node;
	}

	public StructNode getPathSafe(String... path) {

		StructNode node = this;
		try {
			String[] actName = path;
			if (path.length == 1) actName = path[0].split("/");
			for (String n : actName) node = node.getStructSafe(n);
		} catch (StructContainerException e) {
			return null;
		}
		return node;
	}

	public <T extends StructNode> T getStruct(int index) throws StructContainerException {
		return getStruct(Integer.toString(index));
	}

	@SuppressWarnings("unchecked")
	public <T extends String> T getPrimitive(String... name) throws StructContainerException {
		for (String n : name) if (this.primitives.containsKey(n)) return (T) this.primitives.get(n);
		//FIXME
		for (String n : name) if (this.pointers.containsKey(("&"+n))) return (T) this.primitives.get("&"+n);
		String errMsg = "";
		for (String n : name) errMsg += " " + n;
		throw new StructContainerException(this.name+" -> "+errMsg);
	}

	@SuppressWarnings("unchecked")
	public <T extends StructNode> T getStruct(String... name) throws StructContainerException {
		for (String n : name) if (this.structures.containsKey(n)) return (T) this.structures.get(n);
		//FIXME
		for (String n : name) if (this.structures.containsKey(("&"+n))) return (T) this.structures.get("&"+n);
		String errMsg = "";
		for (String n : name) errMsg += " " + n;
		throw new StructContainerException(this.name+" -> "+errMsg);
	}

	@SuppressWarnings("unchecked")
	public <T extends StructNode> T getPath(String... name) throws StructContainerException {
		StructNode actual = this;
		String[] actName = name;
		if (name.length == 1) actName = name[0].split("/");
		for (String n : actName) actual = actual.getStruct(n);
		return (T) actual;
	}

	@SuppressWarnings("unchecked")
	public <T extends String> T getPathPrimitive(String name, String... path) throws StructContainerException {
		return (T) getPath(path).getPrimitive(name);
	}

	@SuppressWarnings("unchecked")
	public <E> E get(String... name) throws StructContainerException {
		for (String n : name) {
			if (primitives.containsKey(n)) return (E) this.primitives.get(n);
			if (structures.containsKey(n)) return (E) this.structures.get(n);
			//FIXME
			if (pointers.containsKey("&"+n)) return (E) this.pointers.get("&"+n);
			if (this.name.equalsIgnoreCase(n)) return (E) this;
		}
		String errMsg = "";
		for (String n : name) errMsg += " " + n;
		throw new StructContainerException(this.name+" -> "+errMsg);
	}

	@SuppressWarnings("unchecked")
	public <T> T getFromPointer(String ... point) throws StructContainerException {
		for (String n : point) if (this.pointers.containsKey(n)) return (T) this.pointers.get(n);
		for (String n : point) if (this.primitives.containsKey(n.substring(1))) return (T) this.primitives.get(n.substring(1));
		for (String n : point) if (this.structures.containsKey(n.substring(1))) return (T) this.structures.get(n.substring(1));
		String errMsg = "";
		for (String n : point) errMsg += " " + n;
		throw new StructContainerException(this.name+" -> "+errMsg);
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
	public boolean containsPointer(String name) {
		return pointers.containsKey(name);
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

	public String[] getPrimitiveArray() {
		String[] childs = this.getPrimitiveChild();
		String[] arr = new String[childs.length];
		for (int i = 0; i < arr.length; i++) arr[i] = this.getPrimitive(childs[i]);
		return arr;
	}

	public StructNode[] getStructArray() {
		String[] childs = this.getStructChild();
		StructNode[] arr = new StructNode[childs.length];
		for (int i = 0; i < arr.length; i++) arr[i] = this.getStruct(childs[i]);
		return arr;
	}

	private static <T> T throwErrMsg(String... msg) throws StructContainerException {
		String errMsg = "";
		for (String n : msg) errMsg += " " + n;
		throw new StructContainerException(errMsg);
	}

	public String printTreeView(int increment, String space, boolean full) {

		String spaces = "";
		for (int i = 0; i < increment; i++) spaces = spaces + "" + space;
		String sName = "";
		if (!this.name.equalsIgnoreCase("imports") || full) {
			sName = spaces + this.name;
			String[] primKeySet = primitives.keySet().toArray(new String[primitives.size()]);
			for (String key : primKeySet)
				sName += "\n" + spaces + "" + space + key + ": " + this.getPrimitive(key);
			String[] structKeySet = structures.keySet().toArray(new String[structures.size()]);
			for (String key : structKeySet) {
				StructNode child = this.getStruct(key);
				sName += "\n" + child.printTreeView(increment + indent, space, full);
			}
		}

		return sName;
	}

	public <T> T smartCastPrimitive(T target, Class targetClass, String name) {
		String value = getPrimitive(name);
		System.out.print(Struct.log_loading ? "SMART CONVERSION: " + name + " = " + value + "\n" : "");
		try {
			Field field = targetClass.getDeclaredField(name);
			field.setAccessible(true);
			Object filedObject = field.get(targetClass.newInstance());
			if (filedObject instanceof Float) field.set(target, getFloat(0, name));
			else if (filedObject instanceof Integer) field.set(target, getInt(0, name));
			else if (filedObject instanceof Boolean) field.set(target, getBool(false, name));
			else if (filedObject instanceof Short) field.set(target, Short.parseShort(value));
			else if (filedObject instanceof Byte) field.set(target, Byte.parseByte(value));
			else if (filedObject instanceof Double) field.set(target, Double.parseDouble(value));
			else if (filedObject instanceof Long) field.set(target, Long.parseLong(value));
			else if (filedObject instanceof String) field.set(target, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return target;
	}

	public <T> T smartCastStruct(T target, Class targetClass, boolean recursive) {
		return smartCastStruct(target, targetClass, recursive, "_");
	}
	public <T> T smartCastStruct(T target, Class targetClass, boolean recursive, String in) {
		return smartCastStruct(target, targetClass, this, recursive, in);
	}
	private <T> T smartCastStruct(T target, Class targetClass, StructNode node, boolean recursive) {
		return smartCastStruct(target, targetClass, node, recursive, "_");
	}
	private <T> T smartCastStruct(T target, Class targetClass, StructNode node, boolean recursive, String in) {
		System.out.print(Struct.log_loading ? in + "IN\n" : "");
		for (String index : node.getStructChild()) {
			try {
				StructNode actualNode = node.getStructSafe(index);
				System.out.print(Struct.log_loading ? in + "1: " + index + "\n" : "");
				Field field = targetClass.getDeclaredField(index);
				field.setAccessible(true);
				Object fieldObject = field.get(targetClass.newInstance());
				if (recursive) fieldObject = smartCastStruct(fieldObject, field.getType(), actualNode, true, in + in);
				for (String i : actualNode.getPrimitiveChild()) {
					System.out.print(Struct.log_loading ? in + "2: " + field.getType() + " : " + i + "\n" + in : "");
					fieldObject = actualNode.smartCastPrimitive(fieldObject, field.getType(), i);
				}
				field.set(target, fieldObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.print(Struct.log_loading ? in + "OUT\n" : "");
		return target;
	}

	public <T> T loadObjectFromStruct(T target, Class targetClass) {
		System.out.print(Struct.log_loading ? "\n" : "");
		for (String prim : getPrimitiveChild()) target = smartCastPrimitive(target, targetClass, prim);
		target = smartCastStruct(target, targetClass, true);
		System.out.print(Struct.log_loading ? "\n" : "");
		return target;
	}

	@Override
	public String toString() {
		return printTreeView(0, space, false);
	}

	public String printFullMem() {
		return printTreeView(0, space, true);
	}
}
