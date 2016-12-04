package test;

import net.henryco.struct.Struct;
import net.henryco.struct.container.tree.StructTree;

/**
 * @author Henry on 04/12/16.
 */
public class MainTest {

	public static final String inDir = "src/examples/";

	public static void main(String[] args) {

		Struct.log_loading = true;

		TestOne one = new TestOne();
		System.out.println(one.getClass());
		System.out.println(new StructTree(inDir+"testOne.struct").mainNode.getStruct("testOne"));
		one = (new StructTree(inDir+"testOne.struct").mainNode.getStruct("testOne")).loadObjectFromStruct(one, TestOne.class);

		System.out.println("FINAL: "+one);
	}
}
