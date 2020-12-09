import java.io.Serializable;

public class Ack implements Serializable {

	private int frameNum;

	public Ack(int frameNum) {
		super();
		this.frameNum = frameNum;
	}

	public int getFrame() {
		return frameNum;
	}

	public void setFrame(int frameNum) {
		this.frameNum = frameNum;
	}

}
