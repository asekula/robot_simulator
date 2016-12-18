package iRobot;

import java.awt.Point;

/**
 * The main of the codebase. Runs the tests. Contains a Brain, Buffer, and
 * Emulator object.
 * 
 * @author alex
 */

public class TestRunner {

	public static void main(String[] args) {
		Brain brain = new Brain();
		Environment environment = new Emulator(); // This line will be changed
													// when we run the code in a
													// real environment.
		DataBuffer buffer = new DataBuffer(environment);
		SensorData sensorData;
		MotorData motorData;

		// Todo: Calibrate orientation and local motor locations. See
		// RobotData.java as for why.
		brain.calibrate(0, new Point(0, 0), new Point(0, 0));

		do {
			sensorData = buffer.getSensorData(); // SensorData may contain noisy
													// data.
			motorData = brain.computeMotorData(sensorData);
			buffer.moveRobotMotors(motorData);
		} while (!brain.isFinished());
	}
}
