import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class Receiver {

	public static final double LOSS = 0.1;

	public static final String HOST = "localhost";

	public static final int PORT = 5000;

	public static void main(String[] args) throws Exception {

		boolean quit = false;
		DatagramSocket socket = new DatagramSocket(PORT);
		while (!quit) {

			byte[] receivedData = new byte[Sender.FRAME_SIZE + 90];

			int waitingFrame = 0;

			ArrayList<Frame> receivedFrames = new ArrayList<Frame>();

			boolean end = false;

			while (!end) {

				System.out.println("Waiting for frame");

				DatagramPacket receivedPack = new DatagramPacket(receivedData, receivedData.length);
				socket.receive(receivedPack);

				Frame frame = (Frame) Serializer.toObject(receivedPack.getData());

				System.out.println("Packet with frame number " + frame.getNum() + " receivedFrames (last: "
						+ frame.isLast() + " )");

				if (frame.getNum() == waitingFrame && frame.isLast()) {

					waitingFrame++;
					receivedFrames.add(frame);

					System.out.println("Last frame received");

					end = true;

				} else if (frame.getNum() == waitingFrame) {
					waitingFrame++;
					receivedFrames.add(frame);
					System.out.println("Frame stored in buffer");
				} else {
					System.out.println("Frame discarded (not in order)");
				}

				Ack ack = new Ack(waitingFrame);

				byte[] ackData = Serializer.toBytes(ack);

				DatagramPacket ackPack = new DatagramPacket(ackData, ackData.length, receivedPack.getAddress(),
						receivedPack.getPort());

				if (Math.random() > LOSS) {
					socket.send(ackPack);
				} else {
					System.out.println("Lost ack with frame number " + ack.getFrame());
				}

				System.out.println("Sending ACK to frame " + waitingFrame + " with " + ackData.length + " bytes");

			}

			System.out.println(" ****** DATA ****** ");

			String data = "";
			for (Frame f : receivedFrames) {
				for (byte b : f.getData()) {
					data += (char) b;
				}
			}
			System.out.println(data);

			quit = data.equals(Sender.QUIT);
		}
	}

}
