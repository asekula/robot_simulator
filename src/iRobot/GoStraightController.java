package iRobot;

public class GoStraightController implements Controller {

	private int count = 0;

	@Override
	public MotorData computeMotorData(SensorData sensorData) {
		count += 1;
		return new MotorData(10, 10);
	}

	@Override
	public boolean isFinished() {
		return (count > 1000);
	}

}
