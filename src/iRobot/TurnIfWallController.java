package iRobot;

public class TurnIfWallController implements Controller {

	private int count = 0;

	@Override
	public MotorData computeMotorData(SensorData sensorData) {
		count += 1;
		if (sensorData.frontIR == -1) {
			return new MotorData(20, 20);
		} else {
			return new MotorData(-10, 10);
		}
	}

	@Override
	public boolean isFinished() {
		return count > 100;
	}

}
