import net.henryco.struct.Struct;
import net.henryco.struct.container.StructContainer;
import net.henryco.struct.container.tree.StructTree;

import java.util.Scanner;

/**
 * src/examples/example2.struct
 * @author Henry on 09/09/16.
 */
public class StaticVoidMainEnter {


    public static void main(String[] args) {
		String cons = fromConsole(">> ");
        StructTree container = StructContainer.tree(Struct.printDataFile(Struct.in.readStructData(cons)), cons);
		System.out.println(container);
    }

    public static String fromConsole(String inMsg) {
        System.out.print(inMsg);
        Scanner in = new Scanner(System.in);
        String url = in.next();
        in.close();
        return url;
    }

    private static void visualise(StructTree container){
        System.out.println(container.mainNode.getStruct("lights").getStruct("containers").getStruct("0").getStruct("type").getPrimitive("0"));
        System.out.println(container.mainNode.getStruct("lights").getStruct("containers").getStruct("0").getStruct("source").
				getStruct("0").getStruct("EscapyShadedLight").getStruct("umbra").getPrimitive("recess"));

        System.out.println(container.mainNode.getStruct("lights").getStruct("containers").getStruct("1").getStruct("type").getPrimitive("0"));
		System.out.println(container.mainNode.getStruct("lights").getStruct("containers").getStruct("1").getStruct("source").
				getStruct("0").getStruct("EscapyStdLight").getStruct("position").getPrimitive("1"));
    }
}
