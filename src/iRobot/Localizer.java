package iRobot;

public class Localizer {

	/*
	 * In calculating the new location in the cell, we need to take into account
	 * the distance the motors traveled (the tachos), the orientation change
	 * (which corresponds to rotation), and the front IR sensor distance. Note
	 * that the left and right IR sensors only give 0/1's.
	 * 
	 * If the front IR sensor value isn't -1 then we can figure out one of the
	 * two coordinates of the motors.
	 * 
	 * Tries to rely as little as possible on the previous locationInCell. This
	 * ensures that if we have to make approximations then small errors won't
	 * snowball into bigger ones.
	 * 
	 * No side effects.
	 */
	public static Point<Double> getNewLocationInCell(SensorData sensorData,
			Point<Double> prevLocation, double orientationAfter,
			double orientationChange) {

		/*
		 * Uses the following data at first priority: front/left/right ir
		 * sensors, trueOrientation.
		 * 
		 * Second priority: orientationChange, tacho values, and previous
		 * location.
		 * 
		 * Basically it tries to rely on the previous location as little as
		 * possible, in order to reduce error. If the front IR sensor returns a
		 * value, and one of the side sensors return a value, then it will know
		 * exactly where it is.
		 */

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

		if (leftDist != -1 && rightDist != -1 && frontDist != -1) {

			if (leftFrontDiffGrid(leftDist, frontDist, orientationAfter)) {
				if (frontRightDiffGrid(frontDist, rightDist,
						orientationAfter)) {
					// Return avg of left/front and front/right.
				} else {
					// Return avg of left/front and left/right.
				}
			} else {
				if (leftRightDiffGrid(leftDist, rightDist, orientationAfter)) {
					// We know that frontRight are on different grid lines.
					// Return avg of left/right and front/right.
				} else {
					// They're all on the same grid line type. Can only use one
					// coordinate.
				}
			}
		}

		// To optimize (if needed), do this last.
		Point<Double> newLocation = curveRobot(prevLocation, orientationAfter,
				orientationChange, sensorData.leftTachoCount,
				sensorData.rightTachoCount);

		// Important: updateCoordinates uses the map where walls are known.
		updateCoordinatesLeftFront(newLocation, leftDist, frontDist,
				orientationAfter);

		// Should be "if different grid line types" where a type is either
		// vertical or horizontal.
		if (leftFrontDiffGrid(leftDist, frontDist, orientationAfter)) {
			return newLocation;
		}

		updateCoordinatesFrontRight(newLocation, frontDist, rightDist,
				orientationAfter);

		if (frontRightDiffGrid(frontDist, rightDist, orientationAfter)) {
			return newLocation;
		}

		updateCoordinatesLeftRight(newLocation, leftDist, rightDist,
				orientationAfter);

		// FINISH.

		return newLocation;
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

	private static Point<Double> curveRobot(Point<Double> location,
			double orientationAfter, double orientationChange, int leftTacho,
			int rightTacho) {

		double orientationBefore = (orientationAfter - orientationChange + 360)
				% 360;
		double leftArc = Geometry.tachoToCM(leftTacho);
		double rightArc = Geometry.tachoToCM(rightTacho);

		return Geometry.curveRobot(location, orientationBefore,
				orientationChange, leftArc, rightArc);
	}

	private static boolean within(double a, double b, double error) {
		return (Math.abs(a - b) <= error);
	}
}
