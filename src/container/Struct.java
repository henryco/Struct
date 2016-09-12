package container;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Henry on 09/09/16.
 */
public class Struct {

    public final String name;
    public Struct parent = null;
    private Map<String, String> primitives = new HashMap<>();
    private Map<String, Struct> structures = new HashMap<>();

    public Struct(String name) {
        this.parent = this;
        this.name = name;
    }
    public Struct(Struct parent, String name) {
        this.parent = parent;
        this.name = name;
    }


    public Struct addPrimitive(String name, String value) {
        this.primitives.put(name, value);
        return this;
    }
    public Struct addStructure(Struct struct){
        return this.structures.put(struct.name, struct);
    }

    public String getPrimitive(String name) {
        return this.primitives.get(name);
    }
    public Struct getStruct(String name){
        return this.structures.get(name);
    }


}
