import net.henryco.struct.Struct;
import net.henryco.struct.container.StructContainer;
import net.henryco.struct.container.tree.StructTree;

import java.util.Scanner;

/**
 * @author Henry on 09/09/16.
 */
public class StaticVoidMainEnter {


    public static void main(String[] args) {

        /*
            src/examples/example2.struct
        */
        StructTree container = StructContainer.tree(Struct.printDataFile(Struct.in.readStructData(inConsole(">> "))));
    }

    public static String inConsole(String inMsg) {
        System.out.print(inMsg);
        Scanner in = new Scanner(System.in);
        String url = in.next();
        in.close();
        return url;
    }

    private static void visualise(StructTree container){
        System.out.println(container.mainNode.getStruct("lights").getStruct("containers").getStruct("1").getStruct("type").getPrimitive("0"));
        System.out.println(container.mainNode.getStruct("lights").getStruct("containers").getStruct("1").getStruct("type").getPrimitive("1"));
        System.out.println(container.mainNode.getStruct("lights").getStruct("containers").getStruct("1").getStruct("type").getPrimitive("2"));
    }
}
