package iRobot;

public interface Controller {
	public MotorData computeMotorData(SensorData sensorData);

	public boolean isFinished();
}
