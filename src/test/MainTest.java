package test;

import net.henryco.struct.Struct;
import net.henryco.struct.container.tree.StructNode;
import net.henryco.struct.container.tree.StructTree;

/**
 * @author Henry on 04/12/16.
 */
public class MainTest {

	public static final String inDir = "src/examples/";
	public static final float floVal = 987f;
	public static Long loVal = 9999933333L;

	public boolean teBool = true;

	public static void main(String[] args) {

		Struct.log_loading = true;

		StructNode node = new StructTree(inDir+"testOne.struct").mainNode.getStruct("testOne");
		System.out.println(node);

		TestConstr test = node.getStruct("TestConstr").instanceAndInvokeObject(new MainTest(), true, true);
		System.out.println(test);

		/*
		 * TEST OUTPUT:

			TestConstr {
				teFloat:		987.0
				teInt:			5
				teStr:			'src/examples/'
				teLong:			1234450
				fieldByte:		15
				fieldShort:		25
				arrBool:		[true, false, true]
			}

		 */
	}


}
