import java.io.Serializable;
import java.util.Arrays;

public class Frame implements Serializable {

	private int num;

	private byte[] data;

	private boolean last;

	public Frame(int num, byte[] data, boolean last) {
		super();
		this.num = num;
		this.data = data;
		this.last = last;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	@Override
	public String toString() {
		return "Frame [num=" + num + ", data=" + Arrays.toString(data) + ", last=" + last + "]";
	}

}
