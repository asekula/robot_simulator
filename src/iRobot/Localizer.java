package iRobot;

public class Localizer {

	/*
	 * Finish.
	 */

	/*
	 * Orientation is the true orientation of the robot. This method uses only
	 * the sensor data to update the robot's location.
	 * 
	 * The number of coordinates we can update (0, 1, or 2) depends on which
	 * values are -1, and which grid line types (horizontal or vertical) the
	 * sensors are detecting.
	 * 
	 * This method is meant to update the small errors that arise from
	 * curveRobot. If there is enough data, then it won't even use the input
	 * location, and return the true location (e.g. when all sensors return
	 * values != -1).
	 * 
	 * Returns nothing, modifies location. Does not modify anything else.
	 * 
	 * Only public interface of Localizer.
	 */
	public static void modifyLocationWithMap(Point<Double> location,
			Point<Integer> currentCell, SensorData sensorData, Map map,
			double orientation) {

		double leftDist = -1.0, rightDist = -1.0, frontDist = -1.0;

		if (sensorData.leftIR != -1) {
			leftDist = sensorData.leftIR
					+ (Constants.DISTANCE_BETWEEN_MOTORS / 2);
		}
		if (sensorData.rightIR != -1) {
			rightDist = sensorData.rightIR
					+ (Constants.DISTANCE_BETWEEN_MOTORS / 2);
		}
		if (sensorData.frontIR != -1) {
			frontDist = sensorData.frontIR + Constants.FRONT_IR_TO_CENTER;
		}

		// If we need to optimize code, there's a lot of computation in here
		// that we can cut down on without losing functionality.

		// Writing ugly code for now. Will revise later.

		// Note: if we have more data than we need, the code below uses all of
		// the data and averages the values it gets.

		if (leftDist != -1 && rightDist != -1 && frontDist != -1) {

			if (leftFrontDiffGrid(leftDist, frontDist, orientation)) {
				if (frontRightDiffGrid(frontDist, rightDist, orientation)) {
					// Return avg of left/front and front/right.
				} else {
					// Return avg of left/front and left/right.
				}
			} else {
				if (leftRightDiffGrid(leftDist, rightDist, orientation)) {
					// We know that frontRight are on different grid lines.
					// Return avg of left/right and front/right.
				} else {
					// They're all on the same grid line type. Can only use one
					// coordinate.
				}
			}
		}

		// Finish.
	}

	private static boolean leftFrontDiffGrid(double left, double front,
			double orientation) {

		if (front == -1 || left == -1)
			return false;

		return !within(Geometry.fullTanInverse(front, left), orientation, 0.5);
	}

	private static boolean frontRightDiffGrid(double front, double right,
			double orientation) {

		if (front == -1 || right == -1)
			return false;

		double angleToRight = Geometry.fullTanInverse(right, front);
		double potentialOrientation = (angleToRight + 90) % 360;
		return !within(potentialOrientation, orientation, 0.5);
	}

	private static boolean leftRightDiffGrid(double left, double right,
			double orientation) {
		// Todo.
		return true;
	}

	/*
	 * Given the left ir value and the front ir value, finds as many coordinates
	 * as it can and updates them in the location point. Doesn't actually use
	 * location point.
	 * 
	 * Modifies location.
	 */
	private static void updateCoordinatesLeftFront(Point<Double> location,
			double left, double front, double orientation) {

		// For now, doing nothing if either length is -1. (maybe change later)
		if (front == -1 || left == -1) {
			return;
		}

		if (leftFrontDiffGrid(left, front, orientation) == false) {
			// Update a single coordinate.

			System.out.println("Left: " + left + ", front: " + front);
			System.out.println("Orientation: " + orientation);

			System.out.println("Left and front are on same grid line.");

			if (orientation < 90 || (orientation > 180 && orientation < 270)) {

				double distanceToWall = Math.sin(Math.toRadians(orientation))
						* front;
				if (distanceToWall > 0) {
					location.y = Constants.CELL_WIDTH - distanceToWall;
				} else {
					location.y = (-1) * distanceToWall;
				}

				System.out.println("Updated y to " + location.y);
				System.out.println(" _________________ ");

				return;
			} else if (orientation > 270
					|| (orientation > 90 && orientation < 180)) {

				double distanceToWall = Math.cos(Math.toRadians(orientation))
						* front;

				if (distanceToWall < 0) {
					location.x = (-1) * distanceToWall;
				} else {
					location.x = Constants.CELL_WIDTH - distanceToWall;
				}

				System.out.println("Updated x to " + location.x);
			} else {
				assert (false); // Shouldn't get here.
			}
		} else {
			// Figure out how to do this.
		}
	}

	private static void updateCoordinatesFrontRight(Point<Double> location,
			double front, double right, double orientation) {

		if (front == -1 || right == -1) {
			return;
		}

	}

	private static void updateCoordinatesLeftRight(Point<Double> location,
			double left, double right, double orientation) {

		if (right == -1 || left == -1) {
			return;
		}

	}

	private static boolean within(double a, double b, double error) {
		return (Math.abs(a - b) <= error);
	}
}
