import struct.Struct;
import struct.container.StructContainer;
import struct.container.tree.StructTree;

/**
 * @author Henry on 09/09/16.
 */
public class StaticVoidMainEnter {


    public static void main(String[] args) {

        StructTree container = StructContainer.tree(Struct.printDataFile(Struct.in.readStructData("src/examples/example2.struct")));
    //    String forOut = container.mainNode.getStruct("one").getStruct("two").getPrimitive("text");
    //    System.out.println(forOut);

    }
}
