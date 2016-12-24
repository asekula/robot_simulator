package iRobot;

import java.applet.*;
import java.awt.*;

/* 
 * Using applet because I'm used to it.
 * Don't really want to learn/use swing.
 * If flashing occurs then we might want to use double buffering.
 */
public class TestApplet extends Applet {

	Emulator environment;
	DataBuffer buffer;
	Brain brain;
	RobotData robotData;
	SensorData sensorData;
	MotorData motorData;

	public void init() {
		environment = new Emulator();
		buffer = new DataBuffer(environment);

		robotData = buffer.calibrate();
		brain = new Brain(robotData);

		// Thread that runs the emulator.
		new Thread() {
			public void run() {
				while (true) {
					environment.moveRobot();
				}
			}
		}.start();

		// Thread that runs the brain/buffer.
		new Thread() {
			public void run() {
				do {
					sensorData = buffer.getSensorData();
					motorData = brain.computeMotorData(sensorData);
					buffer.moveRobotMotors(motorData);
					/*
					 * Note: Assuming that the robot will not move too much in
					 * between reading the sensor data and outputting the motor
					 * data.
					 * 
					 * Todo: Figure out where the delays are necessary.
					 */
				} while (!brain.isFinished());
			}
		}.start();

		// Thread that updates the applet visuals.
		new Thread() {
			public void run() {
				while (true) {
					repaint();

					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void paint(Graphics g) {
		environment.drawEnvironment(g);
	}
}
