package iRobot;

// Really just a struct. When converting to C++ we should make this (and MotorData) a struct.
public class SensorData {

	// Set as public so that we don't need getters, but these really shouldn't
	// be changed.

	/*
	 * Using ints for left/right IR because the value could be -1 (should really
	 * be Option<Boolean>'s but we're converting to C++ anyways).
	 */
	public int leftIR;
	public int rightIR;
	public double frontIR;
	public double IMU;
	public long leftTachoCount;
	public long rightTachoCount;

	public SensorData(int leftIR, int rightIR, double frontIR, double IMU,
			long leftTacho, long rightTacho) {
		this.leftIR = leftIR;
		this.rightIR = rightIR;
		this.frontIR = frontIR;
		this.IMU = IMU;
		this.leftTachoCount = leftTacho;
		this.rightTachoCount = rightTacho;
	}
}