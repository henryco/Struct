import net.henryco.struct.Struct;
import net.henryco.struct.container.StructContainer;
import net.henryco.struct.container.tree.StructTree;

import java.util.Scanner;

/**
 * @author Henry on 09/09/16.
 */
public class StaticVoidMainEnter {


    public static void main(String[] args) {

        /* "src/examples/example2.struct" */
        System.out.print(">> ");
        Scanner in = new Scanner(System.in);
        String url = in.next();
        in.close();


        StructTree container = StructContainer.tree(Struct.printDataFile(Struct.in.readStructData(url)));

        //    System.out.println(container.getStorage().toString());
        //    String forOut = container.mainNode.getStruct("one").getStruct("two").getPrimitive("text");
        //    System.out.println(forOut);

    }
}
