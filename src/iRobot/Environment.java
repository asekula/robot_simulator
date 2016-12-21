package iRobot;

// We will have the same public interface on the robot written in C++.
// (supposedly something that Steve will do)
public interface Environment {

	// Important: Expecting distances in centimeters. We can change this if
	// necessary.

	public int readLeftIR();

	public int readRightIR();

	public double readFrontIR();

	public long readIMU();

	public int readLeftTacho();

	public int readRightTacho();

	public void runMotors(int left, int right);

	public void resetTachoCounts();
}
