package iRobot;

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
}
