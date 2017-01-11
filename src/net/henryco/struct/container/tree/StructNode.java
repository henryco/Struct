package net.henryco.struct.container.tree;

import net.henryco.struct.Struct;
import net.henryco.struct.StructLoadable;
import net.henryco.struct.container.StructContainer;
import net.henryco.struct.container.exceptions.StructContainerException;

import java.lang.reflect.*;
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


	public StructNode removePrimitive(boolean byName, String ... primitive) {
		if (byName) {
			for (String s : primitive)
				if (containsPrimitive(s)) {
					primitives.remove(s);
					return this;
				}
		} else {
			for (String val : getPrimitiveChild())
				if (getPrimitive(val).equalsIgnoreCase(primitive[0])) {
					removePrimitive(true, val);
					return this;
				}
		}
		return this;
	}


	public StructNode removeStruct(String ... nodes) {
		for (String s : nodes)
			if (containsStruct(s)) {
				structures.remove(s);
				return this;
			}
		return this;
	}

	public StructNode removePointer(String ... pointers) {
		for (String s : pointers)
			if (containsPointer(s)) {
				this.pointers.remove(s);
				return this;
			}
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
		throw new StructContainerException(this.name+" ->"+errMsg);
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
			Object filedObject;
			try {
				filedObject = field.get(targetClass.newInstance());
			} catch (InstantiationException e) {
				filedObject = field.get(target);
			}
			if (filedObject instanceof Float) field.set(target, getFloat(0, name));
			else if (filedObject instanceof Integer) field.set(target, getInt(0, name));
			else if (filedObject instanceof Boolean) field.set(target, getBool(false, name));
			else if (filedObject instanceof Short) field.set(target, Short.parseShort(value));
			else if (filedObject instanceof Byte) field.set(target, Byte.parseByte(value));
			else if (filedObject instanceof Double) field.set(target, Double.parseDouble(value));
			else if (filedObject instanceof Long) field.set(target, Long.parseLong(value));
			else if (filedObject instanceof String) field.set(target, value);
		} catch (Exception e) {
			if (Struct.log_loading) e.printStackTrace();
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
		System.out.print(Struct.log_loading ? in + "IN: "+target.getClass().getName()+"\n" : "");
		for (String index : node.getStructChild()) {
			try {
				System.out.println("->"+index);
				if (!index.equalsIgnoreCase("java")) {
					StructNode actualNode = node.getStructSafe(index);
					System.out.print(Struct.log_loading ? in + "1: " + index + "\n" : "");
					Field field = targetClass.getDeclaredField(index);
					field.setAccessible(true);
					Object fieldObject = field.get(targetClass.newInstance());
					String eqName = "";
					if (actualNode.getStructChild().length > 0) {
						eqName = actualNode.getStructChild()[0];
						if (recursive) {
							System.out.println("ENTER: " +actualNode.name +" : "+eqName);
							Class cloF = (eqName.equalsIgnoreCase("java")) ? targetClass : field.getType();
							fieldObject = smartCastStruct(eqName.equalsIgnoreCase("java") ? target : fieldObject, cloF, actualNode, true, in + in);
						}
					}
				//	else {
						for (String i : actualNode.getPrimitiveChild()) {
							System.out.print(Struct.log_loading ? in + "2: " + field.getType() + " : " + i + "\n" + in : "");
							fieldObject = actualNode.smartCastPrimitive(fieldObject, field.getType(), i);
						}
						if (!eqName.equalsIgnoreCase("java")) field.set(target, fieldObject);
				//	}
				} else {
					System.out.println("NODE: "+node.parent.name+"/"+node.name);

					String[] args = node.getPath("java/field").getPrimitiveArray();
					String fieldName = args[args.length - 1];
					String urlName = "";
					for (int i = 0; i < args.length - 1; i++) urlName += (i > 0 && !args[i].startsWith("$")) ? "."+args[i] : args[i];

					System.out.println("NAME: "+target.getClass().getName());
					Class sourceClass = Class.forName(urlName);
					Field sourceField = sourceClass.getDeclaredField(fieldName);
					Field targetField = targetClass.getDeclaredField(node.name);
					sourceField.setAccessible(true);
					targetField.setAccessible(true);
					Object sourceObject = null;
					try {
						sourceObject = sourceField.get(sourceClass.newInstance());
					} catch (Exception e) {
						sourceObject = sourceField.get(sourceClass);
					}

					targetField.set(target, sourceObject);

				}
			} catch (Exception e) {
				if (Struct.log_loading) e.printStackTrace();
			}
		}
		System.out.print(Struct.log_loading ? in + "OUT\n" : "");
		return target;
	}

	/**
	 * Only for BOXED PRIMITIVES TYPE!
	 * @return Object[2]{Class, Object}
	 */
	public Object[] loadInstancedField(Object source) {

		final String[] typeNames = new String[]{"0", "type", "Type"};
		final String[] valueNames = new String[]{"val", "1", "value", "Value"};

		Class targetClass = null;
		Object targetObject = null;
		try {
			return new Object[]{NODE_UTILS.getTargetClass(getString("Void",typeNames)), NODE_UTILS.loadByClass(this, getString("null", typeNames), source, valueNames)};
		} catch (Exception e) {
			try {
				String typoName = getString("null", typeNames);
				if (typoName.startsWith("_array_of_") || typoName.endsWith("_array_of_")) {
					typoName = typoName.replaceAll("_array_of_", "").trim();
					String[] valuesIndex = getStructSafe(valueNames).getChild();
					targetClass = NODE_UTILS.getTargetClass(typoName);

					Object objectArray = Array.newInstance(targetClass, valuesIndex.length);
					for (int i = 0; i < valuesIndex.length; i++) {
						Object arrElement = NODE_UTILS.loadByClass(getStruct(valueNames), typoName, source, valuesIndex[i]);
						Array.set(objectArray, i, arrElement);
					}
					return new Object[]{objectArray.getClass(), objectArray};
				}
			} catch (Exception ex){}
		}
		return null;
	}




	public void invokeFunctions(Object invoker, Object instancer) {

		String[] funcOpt = new String[]{"def", "function", "functions", "func"};
		String[] typeOpt = new String[]{"0", "Type", "type", "class"};
		String[] valsOpt = new String[]{"val", "1", "value", "Val", "Value"};

		StructNode functionsNode = getStructSafe(funcOpt);
		if (functionsNode != null) {
			for (StructNode func : functionsNode.getStructArray()) {
				try {
					String funcName = func.name;
					StructNode[] argsNode = func.getStructArray();
					List<Class> types = new ArrayList<>();
					List<Object> objts = new ArrayList<>();
					if (argsNode != null) {
						for (StructNode argNode : argsNode) {
							Class typo = null;
							Object obj = null;
							try {
								typo = Class.forName(NODE_UTILS.getPrimType(argNode.getString("Void", typeOpt)));
							} catch (Exception ignored){}
							try {
								Method m = Class.class.getDeclaredMethod("getPrimitiveClass", String.class);
								m.setAccessible(true);
								typo = (Class) m.invoke(Class.class.getClass(), argNode.getString("Void", typeOpt));
							} catch (Exception ignored){}
							StructNode pathNode = null;
							if (argNode.getStructSafe(valsOpt) != null)
								if (argNode.getStructSafe(valsOpt).getStructSafe("java") != null) {
								//	pathNode = argNode.getStructSafe(valsOpt).getStructSafe("java").getStructSafe("global");
								//TODO REMOVE LATER
								}
							if (pathNode != null) {
								String pth = NODE_UTILS.getPrimType(pathNode.getString("Void", typeOpt));
								String val = pathNode.getString("Void", "1", "val", "Value", "name");
								Field field = Class.forName(pth).getDeclaredField(val);
								field.setAccessible(true);
								try {
									obj = field.get(Class.forName(pth).newInstance());
								} catch (Exception ignored) {}
							} else {
								Object[] arrg = argNode.loadInstancedField(instancer);
								typo = (Class) arrg[0];
								obj = arrg[1];
							}
							objts.add(obj);
							types.add(typo);
						}
					}
					Class methClass = invoker.getClass();

					try {
						NODE_UTILS.invokeMethod(methClass, funcName, invoker, objts.toArray(new Object[0]), types.toArray(new Class[0]));

					} catch (Exception exc) {
						try {
							Class typear = Class.forName("["+types.get(0).getName());
							Object inar = Array.newInstance(types.get(0), objts.size());
							for (int d = 0; d < objts.size(); d++)
								Array.set(inar, d, objts.get(d));
							NODE_UTILS.invokeMethod(methClass, funcName, invoker, inar, typear);
						} catch (Exception es) {
							try {
								NODE_UTILS.invokeMethod(methClass.getSuperclass(), funcName, invoker, objts.toArray(new Object[0]), types.toArray(new Class[0]));

							} catch (Exception eee) {
								try {
									Class typear = Class.forName("["+types.get(0).getName());
									Object inar = Array.newInstance(types.get(0), objts.size());
									for (int d = 0; d < objts.size(); d++)
										Array.set(inar, d, objts.get(d));
									NODE_UTILS.invokeMethod(methClass.getSuperclass(), funcName, invoker, inar, typear);
								} catch (Exception xfd) {
									System.err.println("BAD INVOKE ALERT!");
									xfd.printStackTrace();
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	public <T extends Object> T instanceAndInvokeObject(Object instancer, boolean loadFields, boolean invokeMethods) {

		String[] consOpt = new String[]{"url", "URL", "link", "class", "package"};

		List<Class> listType = new ArrayList<>();
		List<Object> listOb = new ArrayList<>();
		StructNode[] args = getStructArray();
		for (StructNode arg : args) {
			if (!arg.name.equalsIgnoreCase("function") && !arg.name.equalsIgnoreCase("field")) {
				Object[] arr = arg.loadInstancedField(instancer); //TODO BUGFIX
				Class oclass = (Class) arr[0];
				Object oobj = arr[1];
				listType.add(oclass);
				listOb.add(oobj);
			}
		}
		String cosntructorUrl = getString("error", consOpt) + "." + name;
		Object instancedObject = NODE_UTILS.instanceConstructor(cosntructorUrl, listType.toArray(new Class[0]), listOb.toArray(new Object[0]));

		if (loadFields) {
			StructNode filedNode = getStructSafe("field", "fields");
			if (filedNode != null)
				try {
					instancedObject = filedNode.loadObjectFromStruct(instancedObject, instancedObject.getClass());
				} catch (Exception ignored) {
					ignored.printStackTrace();
				}
		}
		if (invokeMethods) invokeFunctions(instancedObject, instancer);
		return (T) instancedObject;
	}


	public <T> T loadObjectFromStruct(T target, Class targetClass) {
		System.out.print(Struct.log_loading ? "\n" : "");
		for (String prim : getPrimitiveChild()) target = smartCastPrimitive(target, targetClass, prim);
		target = smartCastStruct(target, targetClass, true);
		if (target instanceof StructLoadable) {
			StructLoadable loadable = ((StructLoadable) target).loadFromStruct();
			if (loadable != null) target = (T) loadable;
		}
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




	private static final class NODE_UTILS {

		private static String getPrimType(String word) {
			switch (word) {
				case "boolean": return "java.lang.Boolean";
				case "int": return "java.lang.Integer";
				case "float": return "java.lang.Float";
				case "short": return "java.lang.Short";
				case "long": return "java.lang.Long";
				case "double": return "java.lang.Double";
				case "byte": return "java.lang.Byte";
				case "void": return "java.lang.Void";
			}	return word;
		}


		@SuppressWarnings("unchecked")
		private static <T> T instanceConstructor(String name, Class[] classArr, Object[] objectArr) {
			try {
				Constructor constructor = ClassLoader.getSystemClassLoader().loadClass(name).getDeclaredConstructor(classArr);
				constructor.setAccessible(true);
				System.out.println(constructor.getName() + " : "+constructor.getDeclaringClass().getName());
				return (T) constructor.newInstance(objectArr);
			} catch (Exception e) {
				e.printStackTrace();
				//e.getCause().printStackTrace();
			}
			return null;
		}


		private static Class getTargetClass(String className) {
			Class retClass = null;
			try {
				retClass = ClassLoader.getSystemClassLoader().loadClass(NODE_UTILS.getPrimType(className));
			} catch (Exception ignored){}
			try {
				Method m = Class.class.getDeclaredMethod("getPrimitiveClass", String.class);
				m.setAccessible(true);
				retClass = (Class) m.invoke(Class.class.getClass(), className);
			} catch (Exception ignored){}
			return retClass;
		}


		private static Object loadPrimitiveByClass(String className, String valueName) throws Exception {
			Constructor constructor = ClassLoader.getSystemClassLoader()
					.loadClass(NODE_UTILS.getPrimType(className)).getDeclaredConstructor(String.class);
			constructor.setAccessible(true);
			Object object = constructor.newInstance(valueName);
			if (object instanceof Boolean) return ((Boolean) object).booleanValue();
			if (object instanceof Float) return ((Float) object).floatValue();
			if (object instanceof Integer) return ((Integer) object).intValue();
			if (object instanceof Long) return ((Long) object).longValue();
			if (object instanceof Short) return ((Short) object).shortValue();
			if (object instanceof Double) return ((Double) object).doubleValue();
			if (object instanceof Byte) return ((Byte) object).byteValue();
			return object;
		}


		private static Object loadByClass(StructNode node, String className, Object source, String ... valueNames) throws Exception {

			String[] pathVal = new String[]{"0", "link", "source", "class"};
			String[] valsVal = new String[]{"1", "val", "Value", "name"};

			if (node.getStructSafe(valueNames) != null) {
				StructNode jv = node.getStructSafe(valueNames).getStructSafe("java");
				if (jv != null) {
					StructNode inst = jv.getStructSafe("instanced", "instance");
					if (inst != null) {
						String value = inst.getString("null", "0", "field", "Field");
						Field field = source.getClass().getDeclaredField(value);
						field.setAccessible(true);
						return field.get(source);
					}
					inst = jv.getStructSafe("global", "static");
					if (inst != null) {
						String pth = inst.getString("Void", pathVal);
						String val = inst.getString("Void", valsVal);
						Field field = Class.forName(pth).getDeclaredField(val);
						field.setAccessible(true);
						try {
							return field.get(Class.forName(pth).newInstance());
						} catch (Exception ignored) {}
					}
				}
			}
			return loadPrimitiveByClass(className, node.getString("null", valueNames));
		}

		private static Object invokeMethod(Class methClass, String funcName, Object invoker, Object objts, Class types) throws Exception{
			Method function = methClass.getDeclaredMethod(funcName, types);
			function.setAccessible(true);
			return function.invoke(methClass.cast(invoker), objts);
		}
		private static Object invokeMethod(Class methClass, String funcName, Object invoker, Object[] objts, Class[] types) throws Exception{
			Method function = methClass.getDeclaredMethod(funcName, types);
			function.setAccessible(true);
			return function.invoke(methClass.cast(invoker), objts);
		}
	}
}
