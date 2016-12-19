package iRobot;

/*
 * The main of the codebase. Runs the tests. Contains a Brain, Buffer, and
 * Emulator object.
 */

public class TestRunner {

	public static void main(String[] args) {
		Environment environment = new Emulator(); // This line will be changed
													// when we run the code in a
													// real environment.
		DataBuffer buffer = new DataBuffer(environment);

		RobotData robotData = buffer.calibrate();
		Brain brain = new Brain(robotData);

		SensorData sensorData;
		MotorData motorData;

		do {
			sensorData = buffer.getSensorData(); // SensorData may contain noisy
													// data.
			motorData = brain.computeMotorData(sensorData);
			buffer.moveRobotMotors(motorData);
		} while (!brain.isFinished());
	}
}
