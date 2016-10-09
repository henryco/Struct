package net.henryco.struct.container;

import net.henryco.struct.Struct;
import net.henryco.struct.container.tree.StructNode;
import net.henryco.struct.container.tree.StructTree;

import java.util.List;

/**
 * @author Henry on 19/09/16.
 */
public interface StructContainer {

    static StructTree tree() {
        return new StructTree();
    }
    static StructTree tree(List<String[]>[] data) {
        return new StructTree(data);
    }
	static StructTree tree(String file) {
		return StructContainer.tree(Struct.in.readStructData(file));
	}
	static StructNode loadFromFile(StructNode fileNode, String location, String file, String fileName, String pathName, String ... msg) {
		String loc = location;
		if (fileNode.containsPrimitive(pathName) || fileNode.containsPrimitive("0")) loc = ePrep(fileNode.getPrimitive(pathName, "0"));
		loc += fileNode.getPrimitive(fileName, "1");
		String ms = "";
		for (String s : msg) ms += s;
		System.out.println("\n"+ms+": "+loc);
		return StructContainer.tree(loc).mainNode.getStruct(file);
	}

	static String sPrep(String name) {
		if (!name.startsWith("/")) name = "/" + name;
		return name;
	}
	static String ePrep(String name) {
		if (!name.endsWith("/")) return name + "/";
		return name;
	}
}
