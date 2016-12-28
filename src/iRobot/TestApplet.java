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
	boolean spinningRight = false;

	public void init() {
		emulator = new Emulator();
		buffer = new DataBuffer(emulator);

		robotData = buffer.calibrate();
		brain = new Brain(robotData);

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
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void paint(Graphics g) {
		emulator.drawEnvironment(g, robotData);
		robotData.drawSelf(g);
	}
}
