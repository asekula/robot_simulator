package iRobot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedList;
public class Emulator implements Environment {

	// Todo: Figure out speeds/time and what should go here.

	private double orientation;
	private int motorLSpeed, motorRSpeed; // Q: What speed unit?
	private double frontIR, leftIR, rightIR;
	private int leftTacho;
	private int rightTacho;
	public Point<Double> locationInMaze;
	private Map map;

	public Emulator() {
		orientation = 0; // Set random
		leftIR = -1;
		rightIR = -1;
		frontIR = -1;
		leftTacho = 0;
		rightTacho = 0;
		motorRSpeed = 0;
		motorLSpeed = 0;
		locationInMaze = new Point<Double>(Constants.CELL_WIDTH / 2,
				Constants.CELL_WIDTH / 2); // Set random
		map = new Map(Map.generateRandomMaze());
	}

	/*
	 * Uses the motor speeds to move the robot location and updates its
	 * orientation and sensor data. Primary interface of the emulator, called by
	 * the test applet.
	 */
	public void moveRobot() {

		// Lengths in cm.

		double leftArcLength = motorLSpeed * Constants.TIME_STEP;
		double rightArcLength = motorRSpeed * Constants.TIME_STEP;
		double arcDiff = rightArcLength - leftArcLength;
		double orientationChange = arcDiff / Constants.DISTANCE_BETWEEN_MOTORS;
		orientationChange = Math.toDegrees(orientationChange);
		orientationChange = (orientationChange + 360) % 360;

		// CurveRobot has no side effects, uses no other variables other than
		// those inputted.
		locationInMaze = Geometry.curveRobot(locationInMaze, orientation,
				orientationChange, leftArcLength, rightArcLength);

		orientation += orientationChange;
		orientation = orientation % 360;
		leftTacho += Geometry.cmToTacho(leftArcLength);
		rightTacho += Geometry.cmToTacho(rightArcLength);

		updateIRSensors();

		if (robotHitWall(locationInMaze, orientation)) {
			System.out.println("Error: Robot hit wall.");
		}
	}

	private boolean robotHitWall(Point<Double> location, double orientation) {
		// Todo.
		// Q: Do we even need this? Potential A: It would be good to include it.
		// Yeah
		// It's walked through walls during different trials
		return false;
	}

	/*
	 * Updates left/right IR sensors, and the front IR.
	 */
	private void updateIRSensors() {
		Point<Double> front = getRelativePoint(0, Constants.FRONT_IR_TO_CENTER);
		Point<Double> left = getRelativePoint(90,
				Constants.DISTANCE_BETWEEN_MOTORS / 2);
		Point<Double> right = getRelativePoint(270,
				Constants.DISTANCE_BETWEEN_MOTORS / 2);

		double frontTheta = orientation;
		double leftTheta = (orientation + 90) % 360;
		double rightTheta = (orientation + 270) % 360;

		frontIR = Geometry.getDistanceToNearestWall(front, frontTheta,
				Constants.IR_MAX, map);
		leftIR = Geometry.getDistanceToNearestWall(left, leftTheta,
				Constants.IR_MAX, map);
		rightIR = Geometry.getDistanceToNearestWall(right, rightTheta,
				Constants.IR_MAX, map);

		// Set front/left/right to -1 here to test not having sensor data.
	}

	private Point<Double> getRelativePoint(double theta, double length) {
		return Geometry.getRelativePoint(locationInMaze, orientation, theta,
				length);
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

		// For now, only drawing robot.

		// Saying that the robot is a square of side length DIST_B/W_MOTORS.

		Graphics2D g2 = (Graphics2D) g;

		Point<Integer> currentCell = new Point<Integer>(
				(int) (locationInMaze.x / Constants.CELL_WIDTH),
				(int) (locationInMaze.y / Constants.CELL_WIDTH));

		// Draws the goal location as a yellow circle.
		// drawGoalLocation(g, robotData.nextGoalLocation());
		// drawPath(g, robotData.getCurrentCell(), robotData.getPath());

		// map.drawTrueMaze(g);

		// drawSensors(g);

		g2.setColor(Color.BLACK);
		g2.drawString(currentCell.toString(), 10, 20);
		drawRobot(g2, locationInMaze, orientation);

		g2.setColor(Color.GREEN);
		g2.drawString(robotData.getCurrentCell().toString(), 10, 40);

		// Draws the robot's perceived location.
		drawRobot(g2, robotData.getLocationInMaze(),
				robotData.getTrueOrientation());

		// Draw orientation line.

	}

	private void drawSensors(Graphics g) {
		Point<Double> front = getRelativePoint(0, Constants.FRONT_IR_TO_CENTER);
		Point<Double> left = getRelativePoint(90,
				Constants.DISTANCE_BETWEEN_MOTORS / 2);
		Point<Double> right = getRelativePoint(270,
				Constants.DISTANCE_BETWEEN_MOTORS / 2);

		double frontTheta = orientation;
		double leftTheta = (orientation + 90) % 360;
		double rightTheta = (orientation + 270) % 360;

		g.setColor(Color.BLUE);

		if (frontIR != -1)
			drawSensorLine(g, front, frontTheta, frontIR);
		if (leftIR != -1)
			drawSensorLine(g, left, leftTheta, leftIR);
		if (rightIR != -1)
			drawSensorLine(g, right, rightTheta, rightIR);
	}

	private void drawSensorLine(Graphics g, Point<Double> p, double theta,
			double length) {

		Point<Double> end = new Point<Double>(
				p.x + (Math.cos(Math.toRadians(theta)) * length),
				p.y + (Math.sin(Math.toRadians(theta)) * length));

		g.drawLine((int) (p.x * Constants.SCALE_FACTOR),
				(int) (p.y * Constants.SCALE_FACTOR),
				(int) (end.x * Constants.SCALE_FACTOR),
				(int) (end.y * Constants.SCALE_FACTOR));
	}

	private void drawGoalLocation(Graphics g, Point<Double> goal) {
		int circSize = 4 * Constants.SCALE_FACTOR;
		g.setColor(Color.YELLOW);
		g.fillOval((int) (goal.x * Constants.SCALE_FACTOR) - (circSize / 2),
				(int) (goal.y * Constants.SCALE_FACTOR) - (circSize / 2),
				circSize, circSize);
	}

	private void drawPath(Graphics g, Point<Integer> currentCell,
			LinkedList<Point<Integer>> path) {
		if (path != null) {
			try {
				Iterator<Point<Integer>> iter = path.iterator();

				if (!iter.hasNext())
					return;

				Point<Integer> current = iter.next();

				drawArrow(g, currentCell, current);
				while (iter.hasNext()) {
					Point<Integer> next = iter.next();
					drawArrow(g, current, next);
					current = next;
				}
			} catch (Exception e) {
				System.out.println("Could not draw path.");
			}

		}
	}

	private void drawArrow(Graphics g, Point<Integer> from, Point<Integer> to) {
		double x = Constants.CELL_WIDTH * Constants.SCALE_FACTOR;
		g.drawLine((int) ((from.x + 0.5) * x), (int) ((from.y + 0.5) * x),
				(int) ((to.x + 0.5) * x), (int) ((to.y + 0.5) * x));
	}

	/*
	 * Theta is in degrees.
	 */
	private void drawRobot(Graphics2D g2, Point<Double> location,
			double theta) {
		int scaleFactor = Constants.SCALE_FACTOR;
		g2.rotate(Math.toRadians(theta), location.x * scaleFactor,
				location.y * scaleFactor);

		Point<Integer> pixelLoc = new Point<Integer>(0, 0);
		pixelLoc.x = (int) ((location.x
				- (Constants.DISTANCE_BETWEEN_MOTORS / 2)) * scaleFactor);
		pixelLoc.y = (int) ((location.y
				- (Constants.DISTANCE_BETWEEN_MOTORS / 2)) * scaleFactor);

		g2.drawRect(pixelLoc.x, pixelLoc.y,
				(int) (Constants.DISTANCE_BETWEEN_MOTORS * scaleFactor),
				(int) (Constants.DISTANCE_BETWEEN_MOTORS * scaleFactor));

		g2.rotate(Math.toRadians(-theta), location.x * scaleFactor,
				location.y * scaleFactor);
	}

	public Map getMap() {
		return map;
	}

	// We shouldn't forget to implement random noise in the sensor data.

	// Java should really have read-only properties...
	public double readLeftIR() {
		return leftIR;
	}

	public double readRightIR() {
		return rightIR;
	}

	public double readFrontIR() {
		return frontIR;
	}

	public int readIMU() {
		return (int) orientation;
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
