import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Sender {

	public static final int FRAME_SIZE = 4;

	public static final double LOSS = 0.0;

	public static final int WINDOW_SIZE = 3;

	public static final int TIMEOUT = 120;

	public static final String QUIT = "Quit";

	public static void main(String[] args) throws Exception {

		DatagramSocket socket = new DatagramSocket();

		Scanner scanner = new Scanner(System.in);

		String input = "";

		while (!input.equals(QUIT)) {
			System.out.print("Enter a string: ");
			input = scanner.nextLine();

			int frameNum = 0;

			int lastAck = 0;

			byte[] data = input.getBytes();

			System.out.println("Data size: " + data.length + " bytes");

			int lastFrameNum = (int) Math.ceil((double) data.length / FRAME_SIZE);

			System.out.println("Number of frames to send: " + lastFrameNum);

			InetAddress receiverAddress = InetAddress.getByName(Receiver.HOST);

			ArrayList<Frame> sentFrames = new ArrayList<Frame>();

			while (true) {

				while (frameNum - lastAck < WINDOW_SIZE && frameNum < lastFrameNum) {

					byte[] framData = new byte[FRAME_SIZE];

					framData = Arrays.copyOfRange(data, frameNum * FRAME_SIZE, frameNum * FRAME_SIZE + FRAME_SIZE);

					Frame frame = new Frame(frameNum, framData, (frameNum == lastFrameNum - 1));

					byte[] sendData = Serializer.toBytes(frame);

					DatagramPacket packet = new DatagramPacket(sendData, sendData.length, receiverAddress,
							Receiver.PORT);

					System.out.println(
							"Sending frame with number " + frameNum + " and size " + sendData.length + " bytes");

					sentFrames.add(frame);

					if (Math.random() > LOSS) {
						socket.send(packet);
					} else {
						System.out.println("Lost packet with frame number " + frameNum);
					}

					frameNum++;

				}

				byte[] ackData = new byte[40];

				DatagramPacket ackPack = new DatagramPacket(ackData, ackData.length);

				try {
					socket.setSoTimeout(TIMEOUT);

					socket.receive(ackPack);

					Ack ack = (Ack) Serializer.toObject(ackPack.getData());
					if (ack.getFrame() < lastFrameNum) {
						System.out.println("Received ACK for frame " + ack.getFrame());
					}
					if (ack.getFrame() == lastFrameNum) {
						break;
					}

					lastAck = Math.max(lastAck, ack.getFrame());

				} catch (SocketTimeoutException e) {
					for (int i = lastAck; i < frameNum; i++) {

						byte[] sendData = Serializer.toBytes(sentFrames.get(i));

						DatagramPacket packet = new DatagramPacket(sendData, sendData.length, receiverAddress,
								Receiver.PORT);

						if (Math.random() > LOSS) {
							socket.send(packet);
						} else {
							System.out.println("Lost packet with frame number " + sentFrames.get(i).getNum());
						}

						System.out.println("Re-sending packet with frame number " + sentFrames.get(i).getNum()
								+ " and size " + sendData.length + " bytes");
					}
				}

			}

			System.out.println("Finished sending");

		}
	}
}
