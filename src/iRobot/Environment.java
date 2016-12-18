package iRobot;

// We will have the same public interface on the robot written in C++.
// (supposedly something that Steve will do)
public interface Environment {

	// We shouldn't forget to implement random noise in the sensor data.

	public boolean readLeftIR();

	public boolean readRightIR();

	public int readFrontIR();

	public long readIMU();

	public int readLeftTacho();

	public int readRightTacho();

	public void runMotors(int left, int right);

	public void resetTachoCounts();
}
