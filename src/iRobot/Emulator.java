package iRobot;

public class Emulator implements Environment {

	// We shouldn't forget to implement random noise in the sensor data.

	public boolean readLeftIR() {
		return true;
	}

	public boolean readRightIR() {
		return true;
	}

	public int readFrontIR() {
		return 0;
	}

	public long readIMU() {
		return 0;
	}

	public int readLeftTacho() {
		return 0;
	}

	public int readRightTacho() {
		return 0;
	}

	public void runMotors(int left, int right) {
	}

	public void resetTachoCounts() {
	}
}
