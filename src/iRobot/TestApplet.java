package iRobot;

import java.applet.*;
import java.awt.*;

/* 
 * Using applet because I'm used to it.
 * Don't really want to learn/use swing.
 * If flashing occurs then we might want to use double buffering.
 */
public class TestApplet extends Applet {

	private static final long serialVersionUID = 0; // To silence the warning.

	Emulator emulator;
	DataBuffer buffer;
	Brain brain;
	RobotData robotData;

	public void init() {

		emulator = new Emulator();
		buffer = new DataBuffer(emulator);

		// Thread that runs the emulator.
		new Thread() {
			public void run() {
				while (true) {
					emulator.moveRobot();
					delay();
				}
			}
		}.start();

		// Thread that runs the brain/buffer.
		new Thread() {
			public void run() {
				delay();
				delay();

				robotData = buffer.calibrate();
				brain = new Brain(robotData);

				SensorData sensorData;
				MotorData motorData;
				do {
					sensorData = buffer.getSensorData();
					motorData = brain.computeMotorData(sensorData);
					buffer.moveRobotMotors(motorData);

					/*
					 * Note: Assuming that the robot will not move too much in
					 * between reading the sensor data and outputting the motor
					 * data.
					 */

					/*
					 * Important: If we call getSensorData() too soon after we
					 * just called it, then the tacho values will be so low that
					 * they lose a lot of precision. This small error adds up
					 * (especially if we iterate so quickly) which makes a large
					 * error. To counteract this, delay is called more times.
					 */
					for (int i = 0; i < Constants.EXTRA_ROBOT_DELAY + 1; i++)
						delay();

				} while (!brain.isFinished());
			}
		}.start();

		// Thread that updates the applet visuals.
		new Thread() {
			public void run() {
				while (true) {
					repaint();
					delay();
				}
			}
		}.start();
	}

	/*
	 * Delay so that we can actually look at the simulation.
	 */
	private void delay() {
		try {
			Thread.sleep(Constants.APPLET_DELAY);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Important to note: The coordinate system increases x to the right and
	 * increases y downwards. This makes the orientation seem reversed, and left
	 * appears like right on the screen, whereas it's actually just reflected
	 * across the x-axis, so it's correct.
	 */
	public void paint(Graphics g) {
		emulator.drawEnvironment(g, robotData);
		if (brain != null) {
			brain.getMap().drawRobotMap(g, emulator.getMap());
		}
	}
}
