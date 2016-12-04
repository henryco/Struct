package test;

import net.henryco.struct.StructLoadable;

/**
 * @author Henry on 04/12/16.
 */
public class TestOne implements StructLoadable {

	public static final class inner {
		float val = 12;
		static final float other = 94f;
		static final float other2 = 89;

		public static class next {
			static int none = 10;
			static {
				System.out.println(next.class.getName());
			}
		}
	}

	private int one = 1;
	private int two;
	private float nope = 4f;

	protected String text = "hmm";

	public TestOne() {
	}

	@Override
	public TestOne loadFromStruct() {

		two = 11;
		return this;
	}

	@Override
	public String toString() {
		return "TestOne{" +
				"one=" + one +
				", two=" + two +
				", nope=" + nope +
				", text='" + text + '\'' +
				'}';
	}
}
