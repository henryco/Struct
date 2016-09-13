import container.StructContainer;

/**
 * @author Henry on 09/09/16.
 */
public class MainTMP {



    public static void main(String[] args) {

        Struct.printDataFile(Struct.in.readStructData("src/example.struct"));

        StructContainer container = new StructContainer();

     //   System.out.println(container.mainNode.g);
    }
}
