package iRobot;

import java.awt.Point;

public class DataBuffer {

	Environment environment;

	public DataBuffer(Environment environment) {
		this.environment = environment;
	}

	public SensorData getSensorData() {
		int leftTacho = environment.readLeftTacho();
		int rightTacho = environment.readRightTacho();
		environment.resetTachoCounts();

		return new SensorData(environment.readLeftIR(),
				environment.readRightIR(), environment.readFrontIR(),
				environment.readIMU(), leftTacho, rightTacho);
	}

	public void moveRobotMotors(MotorData motorData) {
		environment.runMotors(motorData.leftMotorValue,
				motorData.rightMotorValue);
	}

	public RobotData calibrate() {
		/*
		 * Todo: Spin the robot around and find the orientation offset, and the
		 * local locations of the left and right motors.
		 */
		return new RobotData(0, new Point(0, 0), new Point(0, 0));
	}
}
