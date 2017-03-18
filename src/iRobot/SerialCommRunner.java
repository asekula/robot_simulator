package iRobot;

/*
 * The main of the codebase. Runs the tests. Contains a Brain, Buffer, and
 * Emulator object.
 * 
 * Note: The code below doesn't use multithreading. This class is really just an
 * outline for what the code will be when we run it on the robot.
 */

public class SerialCommRunner {

	public static void main(String[] args) {
		TwoWaySerialComm comm = new TwoWaySerialComm();
		try {
			comm.connect("COM3");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		Environment environment = comm;
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
			// Q: Should wait between iterations to save computation/have more
			// accurate data? Tacho counts may be too low if we call
			// getSensorData too quickly.
			// (maybe arduino is slow anyways, in which case no need)
		} while (!brain.isFinished());
	}
}
