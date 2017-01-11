package test;

/**
 * @author Henry on 22/12/16.
 */
public class test2 {

	public int r, g, b;
	public String name;

	private float x, y;

	public test2(float x, float y, String name) {
		setName(name);
		setX(x);
		setY(y);
	}

	public test2 setColor(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
		return this;
	}
	public test2 setColor(int[] colors) {
		return setColor(colors[0], colors[1], colors[2]);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "test2{" +
				"\n r=" + r +
				"\n g=" + g +
				"\n b=" + b +
				"\n name='" + name + '\'' +
				"\n x=" + x +
				"\n y=" + y +"\n"+
				'}';
	}
}
