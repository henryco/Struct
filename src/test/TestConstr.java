package test;

import java.util.Arrays;

/**
 * @author Henry on 14/12/16.
 */
public class TestConstr {

	private float teFloat = 0;
	private int teInt = 0;
	private String teStr = "";

	private Long teLong = 0L;

	private boolean[] arrBool = new boolean[0];

	private byte fieldByte = 0;
	private short fieldShort = 0;

	public TestConstr(float teFloat, int teInt, String teStr) {
		this.teFloat = teFloat;
		this.teInt = teInt;
		this.teStr = teStr;
	}

	public TestConstr(float teFloat, int teInt, String teStr, Long teLong) {
		this(teFloat, teInt, teStr);
		this.teLong = teLong;
	}

	public TestConstr(boolean[] arrBool, int teInt, String teStr) {
		this.arrBool = arrBool;
		this.teInt = teInt;
		this.teStr = teStr;
	}

	public void setTeFloat(float teFloat) {
		this.teFloat = teFloat;
	}
	public void setTeInt(int teInt) {
		this.teInt = teInt;
	}
	public void setTeStr(String teStr) {
		this.teStr = teStr;
	}
	public void setTeLong(Long teLong) {
		this.teLong = teLong;
	}
	public void setArrBool(boolean[] arrBool) {
		this.arrBool = arrBool;
	}
	public void setFloatIntStringLong(float fl, int it, String st, Long lg) {
		this.teFloat = fl;
		this.teInt = it;
		this.teStr = st;
		this.teLong = lg;
	}


	@Override
	public String toString() {
		return "TestConstr {" + "\n"+
				" teFloat:\t\t" + teFloat + "\n"+
				" teInt:\t\t\t" + teInt + "\n"+
				" teStr:\t\t\t'" + teStr + '\'' + "\n"+
				" teLong:\t\t" + teLong + "\n"+
				" fieldByte:\t\t" + fieldByte + "\n"+
				" fieldShort:\t" + fieldShort + "\n"+
				" arrBool:\t\t" + Arrays.toString(arrBool) + "\n"+
				'}';
	}
}
