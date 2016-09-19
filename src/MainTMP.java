import container.StructContainer;
import container.tree.StructTree;

/**
 * @author Henry on 09/09/16.
 */
public class MainTMP {



    public static void main(String[] args) {

        StructTree container = StructContainer.tree(Struct.printDataFile(Struct.in.readStructData("src/example2.struct")));
    //    String forOut = container.mainNode.getStruct("one").getStruct("two").getPrimitive("text");
    //    System.out.println(forOut);

    }
}
