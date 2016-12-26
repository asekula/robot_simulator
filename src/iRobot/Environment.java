package iRobot;

// We will have the same public interface on the robot written in C++.
// (supposedly something that Steve will do)
public interface Environment {

	// Important: Expecting distances in centimeters. We can change this if
	// necessary.

	/*
	 * Also important: We don't need that many bytes for the data. To save
	 * memory in the arduino we can shorten these data types could use shorts,
	 * and could avoid doubles. Avoiding doubles may be very useful. (However,
	 * in computing values we still want doubles, but they won't take up
	 * memory.) Tacho counts may be very low anyways, because we reset them
	 * after we read them.
	 */

	// For L/R IRs: -1 means no data, 0 is nothing, 1 is something.
	public int readLeftIR();

	public int readRightIR();

	// -1 if no data, # in cm if data.
	public double readFrontIR();

	public int readIMU();

	public int readLeftTacho();

	public int readRightTacho();

	public void setMotors(int left, int right);

	public void resetTachoCounts();
}
