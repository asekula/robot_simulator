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
	 * Need a way to calibrate orientation offset and locationInCell at the
	 * start of the maze. The robot could do a quick spin around to figure out
	 * which orientation value is exactly aligned with the maze. (If we assume
	 * that it starts out correctly aligned, and it doesn't double check, then
	 * some small error at the start may become a much bigger error later on in
	 * the maze.)
	 * 
	 * Important: Will spin the robot until the right sensor (looks like the
	 * left one in the simulation) hits the back corner. It assumes that, until
	 * the sensor hits the back corner, that the sensor value will increase. We
	 * should place the robot accordingly (i.e. the orientation should be
	 * slightly positive when we put it down).
	 */
	public RobotData calibrate() {
		/*
		 * Todo: Spin the robot around and find the orientation offset, and the
		 * location in the current tile.
		 */

		double prevRightIR = environment.readRightIR();
		double prevIMU = environment.readIMU(); // OriginalIMU is 0.
		double originalRightIR = prevRightIR;

		System.out.println(originalRightIR);

		environment.setMotors(1, -1); // Should be slowest possible.

		double nextRightIR;

		while ((nextRightIR = environment.readRightIR()) >= prevRightIR) {
			prevRightIR = nextRightIR;
			prevIMU = environment.readIMU();

			// Remove this in the real robot.
			// This is required for the emulator.
			try {
				Thread.sleep(1);
			} catch (Exception e) {
			}
		}

		environment.setMotors(0, 0);

		// When fixing sensor location, do it here.
		double side1 = originalRightIR
				+ (Constants.DISTANCE_BETWEEN_MOTORS / 2);
		double side2 = prevRightIR + (Constants.DISTANCE_BETWEEN_MOTORS / 2);

		System.out.println("Side1: " + side1);
		System.out.println("Side2: " + side2);
		System.out.println("Angle: " + (360 - prevIMU));

		// Side-Angle-Side
		double offset = Geometry.calculateOffset(side1, 360 - prevIMU, side2);
		Point<Double> location = Geometry.calculateStartingLocation(side1,
				360 - prevIMU, side2); // Side2 hits the back corner.

		System.out.println("Robot offset: " + offset);
		System.out.println("Robot location: " + location);
		return new RobotData(offset, location);
	}
}
