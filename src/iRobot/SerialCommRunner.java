package iRobot;

public class SerialCommRunner {

	public static void main(String[] args) {
		try {
			(new TwoWaySerialComm()).connect("COM3");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
