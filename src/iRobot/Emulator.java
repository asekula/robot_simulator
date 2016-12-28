package iRobot;

import java.awt.*;

public class Emulator implements Environment {

	// Todo: Figure out speeds/time and what should go here.
	private static double TIME_STEP = 0.03; // No idea what to put here.

	private double orientation;
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

		updateIRSensors(); // Todo: Test this (do this later).

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
	 * 
	 * Maybe clean this code up (and maybe merge into code above).
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
	private Point<Double> getRelativePoint(Point<Double> p, double orientation,
			double theta, double length) {
		double absRadians = Math.toRadians((orientation + theta + 360) % 360);
		double locX = p.x + (Math.cos(absRadians) * length);
		double locY = p.y + (Math.sin(absRadians) * length);

		return new Point<Double>(locX, locY);
	}

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
		int scaleFactor = Constants.SCALE_FACTOR;

		// Saying that the robot is a square of side length DIST_B/W_MOTORS.

		Graphics2D g2 = (Graphics2D) g;

		Point<Integer> currentCell = new Point<Integer>(
				(int) (locationInMaze.x / Constants.CELL_WIDTH),
				(int) (locationInMaze.y / Constants.CELL_WIDTH));

		g2.drawString(currentCell.toString(), 10, 20);
		g2.setColor(Color.GREEN);
		g2.drawString(robotData.getCurrentCell().toString(), 10, 40);
		g2.setColor(Color.BLACK);
		map.drawMaze(g);

		g2.rotate(Math.toRadians(orientation), locationInMaze.x * scaleFactor,
				locationInMaze.y * scaleFactor);
		g2.drawRect(
				(int) ((locationInMaze.x
						- (Constants.DISTANCE_BETWEEN_MOTORS / 2))
						* scaleFactor),
				(int) ((locationInMaze.y
						- (Constants.DISTANCE_BETWEEN_MOTORS / 2))
						* scaleFactor),
				(int) (Constants.DISTANCE_BETWEEN_MOTORS * scaleFactor),
				(int) (Constants.DISTANCE_BETWEEN_MOTORS * scaleFactor));

		// Draw orientation line.

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
