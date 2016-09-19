import container.StructContainer;

/**
 * @author Henry on 09/09/16.
 */
public class MainTMP {



    public static void main(String[] args) {

        StructContainer container = new StructContainer(Struct.printDataFile(Struct.in.readStructData("src/example2.struct")));

      //  String forOut = container.mainNode.getStruct("one").getStruct("two").getPrimitive("text");
      //  System.out.println(forOut);
        
    }
}
