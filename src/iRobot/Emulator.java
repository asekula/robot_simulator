package iRobot;

import java.awt.*;

public class Emulator implements Environment {

	// Todo: Figure out speeds/time and what should go here.
	private static double TIME_STEP = 0.05; // No idea what to put here.

	private double orientation;
	private int motorLSpeed, motorRSpeed; // Q: What speed unit?
	private double frontIR, leftIR, rightIR;
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
		double arcDiff = rightArcLength - leftArcLength;
		double orientationChange = arcDiff / Constants.DISTANCE_BETWEEN_MOTORS;
		orientationChange = Math.toDegrees(orientationChange);
		orientationChange = (orientationChange + 360) % 360;

		// CurveRobot has no side effects, uses no other variables other than
		// those inputted.
		locationInMaze = curveRobot(locationInMaze, orientation,
				orientationChange, leftArcLength, rightArcLength);

		orientation += orientationChange;
		orientation = orientation % 360;
		leftTacho += cmToTacho(leftArcLength);
		rightTacho += cmToTacho(rightArcLength);

		updateIRSensors();

		if (robotHitWall(locationInMaze, orientation)) {
			System.out.println("Error: Robot hit wall.");
		}
	}

	private boolean robotHitWall(Point<Double> location, double orientation) {
		// Todo.
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

		frontIR = getDistanceToNearestWall(front, frontTheta, Constants.IR_MAX);
		leftIR = getDistanceToNearestWall(left, leftTheta, Constants.IR_MAX);
		rightIR = getDistanceToNearestWall(right, rightTheta, Constants.IR_MAX);
	}

	/*
	 * See comment below. This method is generalized.
	 */
	private Point<Double> getRelativePoint(Point<Double> p, double orientation,
			double theta, double length) {
		double absRadians = Math.toRadians((orientation + theta + 360) % 360);
		double locX = p.x + (Math.cos(absRadians) * length);
		double locY = p.y + (Math.sin(absRadians) * length);

		return new Point<Double>(locX, locY);
	}

	/*
	 * Theta is in degrees. From the locationInMaze it calculates a point
	 * relative to it that is length distance away, in direction theta relative
	 * to it. (If the robot is facing north, theta = 90 would return a point
	 * exactly west of the robot.)
	 */
	private Point<Double> getRelativePoint(double theta, double length) {
		return getRelativePoint(locationInMaze, orientation, theta, length);
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
	 * 
	 * Important: Assuming that maxDist <= 15. This ensures that no matter where
	 * the sensor is located, there will be a maximum of two walls that will be
	 * in its line of sight. In other words, we only need to check for the
	 * existence of two walls, and if neither exist then we know that the sensor
	 * did not detect a wall.
	 */
	private double getDistanceToNearestWall(Point<Double> location,
			double orientation, double maxDist) {

		assert (maxDist < Constants.CELL_WIDTH); // See important comment above.

		// Simpler if we handle these cases seperately.
		if (orientation == 0 || orientation == 90 || orientation == 180
				|| orientation == 270) {

			Direction dir = Direction.getDirection(orientation);
			return getNearestWallInDirection(location, dir, maxDist);
		} else {
			// Line along which we're looking: y = mx + b
			double m = Math.tan(Math.toRadians(orientation));
			double b = location.y - (location.x * m);

			assert (m != 0); // Handled these cases above.

			// Want to find intersection of the line with two grid lines.
			// (grid lines are lines where walls can be located)
			double xGridLine = getXGridLine(location.x, orientation);
			double yGridLine = getYGridLine(location.y, orientation);

			Point<Double> xGridLinePoint = new Point<Double>(xGridLine,
					xGridLine * m + b);
			double xGridLineDistance = distanceBetween(location,
					xGridLinePoint);

			// x = (y - b) / m. Assuming m != 0.
			Point<Double> yGridLinePoint = new Point<Double>(
					(yGridLine - b) / m, yGridLine);
			double yGridLineDistance = distanceBetween(location,
					yGridLinePoint);

			boolean wallAtXGridLine = map.wallAt(xGridLinePoint);
			boolean wallAtYGridLine = map.wallAt(yGridLinePoint);

			if (wallAtXGridLine && wallAtYGridLine) {
				double nearest = Math.min(xGridLineDistance, yGridLineDistance);
				if (nearest <= maxDist) {
					return nearest;
				}
			} else if (wallAtXGridLine && xGridLineDistance <= maxDist) {
				return xGridLineDistance;
			} else if (wallAtYGridLine && yGridLineDistance <= maxDist) {
				return yGridLineDistance;
			}

			return -1;
		}
	}

	/*
	 * Edge case of the method above. If a wall exists in the current direction,
	 * it will be one of the four possible walls of the current cell.
	 * 
	 * Returns -1 if there is no wall in the direction.
	 */
	private double getNearestWallInDirection(Point<Double> p, Direction dir,
			double maxDist) {
		Point<Double> gridLinePoint = new Point<Double>(0.0, 0.0);
		if (dir == Direction.EAST || dir == Direction.WEST) {
			double theta = 0;
			if (dir == Direction.WEST)
				theta = 180;

			gridLinePoint.x = getXGridLine(p.x, theta);
			gridLinePoint.y = p.y;
		} else {
			double theta = 90;
			if (dir == Direction.SOUTH)
				theta = 270;

			gridLinePoint.x = p.x;
			gridLinePoint.y = getYGridLine(p.y, theta);
		}

		if (map.wallAt(gridLinePoint)) {
			double dist = distanceBetween(p, gridLinePoint);
			if (dist <= maxDist)
				return dist;
		}

		return -1;
	}

	private double distanceBetween(Point<Double> p1, Point<Double> p2) {
		return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
	}

	/*
	 * Theta is orientation (not relative), y is the location's y coordinate.
	 */
	private double getYGridLine(double y, double theta) {
		if (theta > 0 && theta < 180) {
			return Math.ceil(y / Constants.CELL_WIDTH) * Constants.CELL_WIDTH;
		} else {
			return Math.floor(y / Constants.CELL_WIDTH) * Constants.CELL_WIDTH;
		}
	}

	private double getXGridLine(double x, double theta) {
		if (theta < 90 || theta > 270) {
			return Math.ceil(x / Constants.CELL_WIDTH) * Constants.CELL_WIDTH;
		} else {
			return Math.floor(x / Constants.CELL_WIDTH) * Constants.CELL_WIDTH;
		}
	}

	/*
	 * OrientationChange is relative to current orientation, and is positive
	 * between 0 and 359.
	 */
	private Point<Double> curveRobot(Point<Double> location,
			double orientationBefore, double orientationChange, double leftArc,
			double rightArc) {

		System.out.println("In emulator:" + leftArc + ", " + rightArc);

		if (orientationChange == 0) {
			return getRelativePoint(location, orientationBefore, 0, leftArc);
		}

		double theta = orientationChange;
		double phi; // In degrees.
		if (orientationChange > 180) {
			theta = 360 - orientationChange;
		}

		phi = (180 - theta) / 2;

		// Theta guaranteed to be <= 180.
		// Phi guaranteed to be <= 90.

		double longRadius = Math.max(leftArc, rightArc) / Math.toRadians(theta);
		double radiusToCenter;

		longRadius = Math.abs(longRadius);
		radiusToCenter = longRadius - (Constants.DISTANCE_BETWEEN_MOTORS / 2);

		double halfTheta = Math.toRadians(theta / 2); // In radians.
		double hypotenuse = Math.sin(halfTheta) * radiusToCenter * 2;
		double relativeAngle = 90 - phi;

		if (orientationChange <= 180) {
			return getRelativePoint(location, orientationBefore, relativeAngle,
					hypotenuse);
		} else {
			return getRelativePoint(location, orientationBefore,
					360 - relativeAngle, hypotenuse);
		}
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
		drawGoalLocation(g, robotData.nextGoalLocation());
		map.drawMaze(g);

		drawSensors(g);

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
