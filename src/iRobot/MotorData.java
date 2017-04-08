package iRobot;

// Just a struct containing MotorData.
public class MotorData {
	public int leftMotorValue;
	public int rightMotorValue;

	public MotorData(int left, int right) {
		this.leftMotorValue = left;
		this.rightMotorValue = right;
	}

	@Override
	public String toString() {
		return leftMotorValue + "," + rightMotorValue;
	}
}
