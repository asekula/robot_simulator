package iRobot;

public class TurnIfWallController implements Controller {

	private boolean isTurning = false;

	@Override
	public MotorData computeMotorData(SensorData sensorData) {
		if(sensorData.frontIR < 70) {
			isTurning = true;
		}

		if (sensorData.frontIR > 100) {
			isTurning = false;
		}		

		if (!isTurning) {
			return new MotorData(50, 50);
		} else {
			return new MotorData(0, -40);
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}

}