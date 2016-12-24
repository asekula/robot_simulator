package iRobot;

public class DataBuffer {

	Environment environment;

	public DataBuffer(Environment environment) {
		this.environment = environment;
	}

	public SensorData getSensorData() {
		int leftTacho = environment.readLeftTacho();
		int rightTacho = environment.readRightTacho();
		environment.resetTachoCounts(); // Note: Resetting tacho counts.

		return new SensorData(environment.readLeftIR(),
				environment.readRightIR(), environment.readFrontIR(),
				environment.readIMU(), leftTacho, rightTacho);
	}

	public void moveRobotMotors(MotorData motorData) {
		environment.setMotors(motorData.leftMotorValue,
				motorData.rightMotorValue);
	}

	/*
	 * Need a way to calibrate orientaion offset and locationInCell at the start
	 * of the maze. The robot could do a quick spin around to figure out which
	 * orientation value is exactly aligned with the maze. (If we assume that it
	 * starts out correctly aligned, and it doesn't double check, then some
	 * small error at the start may become a much bigger error later on in the
	 * maze.)
	 */
	public RobotData calibrate() {
		/*
		 * Todo: Spin the robot around and find the orientation offset, and the
		 * location in the current tile.
		 */
		return new RobotData(0, new Point<Double>(0.0, 0.0));
	}
}
