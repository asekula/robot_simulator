package iRobot;

public class GoStraightController implements Controller {

	private int count = 0;

	@Override
	public MotorData computeMotorData(SensorData sensorData) {
		count += 1;
		if ((count % 1000) < 500) {
			return new MotorData(100, 100);
		} else {
			return new MotorData(-100, -100);
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}

}
