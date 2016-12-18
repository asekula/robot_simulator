package iRobot;

// Really just a struct. When converting to C++ we should make this (and MotorData) a struct.
public class SensorData {

	// Set as public so that we don't need getters, but these really shouldn't
	// be changed.
	public boolean leftIR;
	public boolean rightIR;
	public int frontIR;
	public double IMU;
	public long leftTachoCount;
	public long rightTachoCount;

	public SensorData(boolean leftIR, boolean rightIR, int frontIR, double IMU,
			long leftTacho, long rightTacho) {
		this.leftIR = leftIR;
		this.rightIR = rightIR;
		this.frontIR = frontIR;
		this.IMU = IMU;
		this.leftTachoCount = leftTacho;
		this.rightTachoCount = rightTacho;
	}
}