package iRobot;

import java.awt.*;

public class Emulator implements Environment {

	// Todo: Figure out speeds/time and what should go here.
	private static double TIME_STEP = 2; // No idea what to put here.

	private int orientation;
	private int motorLSpeed, motorRSpeed; // Q: What speed unit?
	private int leftIR, rightIR;
	private double frontIR;
	private int leftTacho;
	private int rightTacho;
	private Point<Double> locationInMaze;
	private Map map;

	public Emulator() {
		orientation = 0;
		leftIR = 1;
		rightIR = 1;
		frontIR = -1;
		leftTacho = 0;
		rightTacho = 0;
		motorRSpeed = 0;
		motorLSpeed = 0;
		locationInMaze = new Point<Double>(Constants.CELL_WIDTH / 2,
				Constants.CELL_WIDTH / 2);
		map = new Map();
		map.generateRandomMaze();
	}

	/*
	 * Uses the motor speeds to move the robot location and updates its
	 * orientation and sensor data. Primary interface of the emulator, called by
	 * the test applet.
	 */
	public void moveRobot() {

		// Lengths in cm.
		double leftArcLength = motorLSpeed * TIME_STEP;
		double rightArcLength = motorRSpeed * TIME_STEP;

		// 1 if they are the same sign, -1 if opposite signs.
		int sign = sameSign(motorLSpeed, motorRSpeed);

		leftTacho += cmToTacho(leftArcLength);
		rightTacho += cmToTacho(rightArcLength);

		// theta is positive if turning left, negative if right.
		double theta = (rightArcLength + (sign * (-1) * leftArcLength))
				/ Constants.DISTANCE_BETWEEN_MOTORS;

		// Only changes locationInCell.
		curveRobot(theta, leftArcLength, rightArcLength);

		orientation += theta;

		updateIRSensors();
	}

	/*
	 * Updates left/right IR sensors, and the front IR.
	 */
	private void updateIRSensors() {

		updateLRSensor(90);
		updateLRSensor(270);

		Point<Double> sensorLocation = getRelativePoint(0,
				Constants.FRONT_IR_TO_CENTER);
		double sensorOrientation = orientation;
		double distanceToWall = getDistanceToNearestWall(sensorLocation,
				sensorOrientation, Constants.FRONT_MAX_DISTANCE);
		frontIR = distanceToWall; // If distance is -1, still set frontIR to -1.
	}

	/*
	 * Updates a single LR sensor. angle should be either 90 (left) or 270
	 * (right). This is really just to reuse code instead of having the same
	 * chunk of code twice in updateIRSensors. Angle is in degrees.
	 */
	private void updateLRSensor(int angle) {
		Point<Double> sensorLocation;
		double sensorOrientation;
		double distanceToWall;

		sensorLocation = getRelativePoint(angle,
				Constants.DISTANCE_BETWEEN_MOTORS / 2);
		sensorOrientation = (orientation + angle) % 360;
		distanceToWall = getDistanceToNearestWall(sensorLocation,
				sensorOrientation, Constants.LR_MAX_DISTANCE);

		// Distance is -1 if there is no wall within MAX_DISTANCE.
		if (distanceToWall == -1) {
			if (angle == 90) {
				leftIR = 0;
			} else if (angle == 270) {
				rightIR = 0;
			}

		} else {
			if (angle == 90) {
				leftIR = 1;
			} else if (angle == 270) {
				rightIR = 1;
			}
		}
	}

	/*
	 * Theta is in degrees. From the locationInMaze it calculates a point
	 * relative to it that is length distance away, in direction theta relative
	 * to it. (If the robot is facing north, theta = 90 would return a point
	 * exactly west of the robot.)
	 */
	private Point<Double> getRelativePoint(double theta, double length) {
		double locX = locationInMaze.x
				+ (Math.cos(Math.toRadians((orientation + theta) % 360))
						* length);
		double locY = locationInMaze.y
				+ (Math.sin(Math.toRadians((orientation + theta) % 360))
						* length);

		return new Point<Double>(locX, locY);
	}

	/*
	 * Converts the arc length in cm to the number of tachos that the motor did
	 * to travel that arc.
	 */
	private int cmToTacho(double cm) {
		double circumference = Constants.WHEEL_DIAMETER * Math.PI; // In cm.
		return (int) (cm * Constants.TACHOS_PER_ROTATION / circumference);
	}

	/*
	 * From the location looking at the direction orientation, find the distance
	 * to the nearest wall. Uses map. Doesn't alter any variables. Note that the
	 * orientation is not relative.
	 * 
	 * Returns -1 if there is no wall within the maxDist, otherwise returns the
	 * distance in cm.
	 */
	private double getDistanceToNearestWall(Point<Double> location,
			double orientation, double maxDist) {
		// Todo.
		return 0;
	}

	private void curveRobot(double theta, double leftArc, double rightArc) {
		// Todo.
	}

	/*
	 * If a and b are the same sign, then this returns 1, otherwise it returns
	 * -1. Treats 0 as having both signs.
	 */
	private int sameSign(int a, int b) {
		if ((a * b) >= 0)
			return 1;
		else
			return -1;
	}

	/*
	 * Draws the map and current location of the robot to the graphics.
	 * 
	 * Optional: Make walls green if the robot discovered them, make walls red
	 * if the robot thinks it's not a wall (or make a non-wall red if it thinks
	 * its a wall), and black if it hasn't discovered it yet. Also draws where
	 * the robot thinks it is in a different color, so we can visually compare
	 * how well the localization code works.
	 */
	public void drawEnvironment(Graphics g, RobotData robotData) {
		Graphics2D g2 = (Graphics2D) g;
		g2.rotate(Math.toRadians(orientation), locationInMaze.x * 5 + 25,
				locationInMaze.y * 5 + 25);
		g2.drawRect((int) (locationInMaze.x * 5), (int) (locationInMaze.y * 5),
				50, 50); // Saying that robot is a 10cm/10cm rectangle.

		// Todo: Finish.
	}

	// We shouldn't forget to implement random noise in the sensor data.

	// Java should really have read-only properties...
	public int readLeftIR() {
		return leftIR;
	}

	public int readRightIR() {
		return rightIR;
	}

	public double readFrontIR() {
		return frontIR;
	}

	public int readIMU() {
		return orientation;
	}

	public int readLeftTacho() {
		return leftTacho;
	}

	public int readRightTacho() {
		return rightTacho;
	}

	public void resetTachoCounts() {
		leftTacho = 0;
		rightTacho = 0;
	}

	// For now, these values are speed.
	public void setMotors(int left, int right) {
		motorLSpeed = left;
		motorRSpeed = right;
	}
}
