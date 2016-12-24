package iRobot;

import java.awt.*;

public class Emulator implements Environment {

	// We shouldn't forget to implement random noise in the sensor data.

	public int readLeftIR() {
		return -1;
	}

	public int readRightIR() {
		return -1;
	}

	public double readFrontIR() {
		return 0;
	}

	public int readIMU() {
		return 0;
	}

	public int readLeftTacho() {
		return 0;
	}

	public int readRightTacho() {
		return 0;
	}

	// For now, these values are speed.
	public void setMotors(int left, int right) {
	}

	public void resetTachoCounts() {
	}

	public void moveRobot() {

	}

	public void drawEnvironment(Graphics g) {

	}
}
