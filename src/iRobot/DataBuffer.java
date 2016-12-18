package iRobot;

public class DataBuffer {

	Environment environment;

	public DataBuffer(Environment environment) {
		this.environment = environment;
	}

	public SensorData getSensorData() {
		return new SensorData(environment.readLeftIR(),
				environment.readRightIR(), environment.readFrontIR(),
				environment.readIMU(), environment.readLeftTacho(),
				environment.readRightTacho());
	}

	public void moveRobotMotors(MotorData motorData) {
		environment.runMotors(motorData.leftMotorValue,
				motorData.rightMotorValue);
	}
}
