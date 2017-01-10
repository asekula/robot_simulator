package iRobot;

public class InstructionGenerator {

	public static int STRAIGHT_VALUE = 4; // Q: What to put here?
	public static int ROTATION_VALUE = 2; // ^Same

	public static MotorData generateExploringMotorData(RobotData robotData) {
		/*
		 * To start out this can be very simple. All it requires is looking at
		 * the motors' local coordinates, the orientation, and the next tile in
		 * the path, and calculating the arc required to get the motors along
		 * the tile border between the current tile and the next tile.
		 */

		/*
		 * Later we can incorporate looking at the next two cells and figure out
		 * what the orientation should be when it enters the next cell to make
		 * it easiest to turn to the one after that.
		 */

		/*
		 * Note: Doing a very simple implementation. Either rotating or moving
		 * in a straight line. Will eventually redo this.
		 */

		Point<Double> next = robotData.nextGoalLocation();

		// If no next cell, don't move. (i.e. if it hasn't figured out where to
		// go yet).

		if (next.x == -1) {
			return new MotorData(ROTATION_VALUE, -ROTATION_VALUE);
		}

		Point<Double> current = robotData.getLocationInMaze();
		double orientation = robotData.getTrueOrientation();

		double rotate = angleToRotate(current, next, orientation);

		if (rotate <= 2 || rotate >= 358) {
			return new MotorData(STRAIGHT_VALUE, STRAIGHT_VALUE);
		} else {
			int rotatingMultiplier;// 1 if needs to rotate left, -1 if right.

			if (rotate >= 180) {
				rotatingMultiplier = -1;
			} else {
				rotatingMultiplier = 1;
			}

			if (rotate <= 20 || rotate >= 340) {

				int curveFactor;

				if (rotate <= 20) {
					curveFactor = (int) rotate;
				} else {
					curveFactor = (int) (rotate - 360);
				}

				curveFactor /= 4;

				return new MotorData(STRAIGHT_VALUE + (curveFactor * -1),
						STRAIGHT_VALUE + curveFactor);
			} else {
				return new MotorData(ROTATION_VALUE * rotatingMultiplier * (-1),
						ROTATION_VALUE * rotatingMultiplier);
			}
		}
	}

	public static MotorData generateSolverMotorData(RobotData robotData) {

		Point<Integer> current = robotData.getCurrentCell();
		Point<Integer> next = robotData.nextCell();

		if (next.x == -1)
			return new MotorData(0, 0);

		Point<Integer> afterNext = robotData.secondNextCell();

		double desiredOrientation = getDesiredOrientation(current, afterNext);
		Point<Double> goalLocation = getGoalLocation(current, next);

		return motorDataToCurveRobot(robotData.getLocationInMaze(),
				goalLocation, robotData.getTrueOrientation(),
				desiredOrientation);
	}

	private static double getDesiredOrientation(Point<Integer> first,
			Point<Integer> third) {
		int dx = third.x - first.x;
		int dy = third.y - first.y;
		return Geometry.fullTanInverse(dx, dy);
	}

	public static Point<Double> getGoalLocation(Point<Integer> current,
			Point<Integer> next) {
		Direction directionTo = current.directionTo(next);
		switch (directionTo) {
			case EAST :
				return new Point<Double>((current.x + 1) * Constants.CELL_WIDTH,
						(current.y + 0.5) * Constants.CELL_WIDTH);

			case NORTH :
				return new Point<Double>(
						(current.x + 0.5) * Constants.CELL_WIDTH,
						(current.y + 1) * Constants.CELL_WIDTH);

			case SOUTH :
				return new Point<Double>(
						(current.x + 0.5) * Constants.CELL_WIDTH,
						current.y * Constants.CELL_WIDTH);

			case WEST :
				return new Point<Double>(current.x * Constants.CELL_WIDTH,
						(current.y + 0.5) * Constants.CELL_WIDTH);

		}
		return new Point<Double>(-1.0, -1.0);
	}

	private static final double A_SCALE = 10; // What to put here?
	private static final double B_SCALE = 10; // What to put here?

	private static MotorData motorDataToCurveRobot(Point<Double> current,
			Point<Double> goal, double theta, double desiredTheta) {

		// double a = 0, b = 0;

		double rotate = angleToRotate(current, goal, theta);

		int rotatingMultiplier;// 1 if needs to rotate left, -1 if right.

		if (rotate <= 2 || rotate >= 358) {
			return new MotorData(STRAIGHT_VALUE, STRAIGHT_VALUE);
		} else {
			if (rotate >= 180) {
				rotatingMultiplier = -1;
			} else {
				rotatingMultiplier = 1;
			}

			if (rotate <= 60 || rotate >= 300) {

				int curveFactor;

				if (rotate <= 60) {
					curveFactor = (int) rotate;
				} else {
					curveFactor = (int) (rotate - 360);
				}

				curveFactor /= 4;

				return new MotorData(STRAIGHT_VALUE + (curveFactor * -1),
						STRAIGHT_VALUE + curveFactor);
			} else {
				return new MotorData(ROTATION_VALUE * rotatingMultiplier * (-1),
						ROTATION_VALUE * rotatingMultiplier);
			}
		}

		// b is directly proportional with the difference between theta and
		// desired theta. (and theta and angleToRotate)

		// if the angle difference is too large, a is smaller.
		// otherwise a is just a constant

		// returns (a + b, a - b) where b depends on how much rotation is
		// needed, and a depends on how much space needs to be covered.
		// return new MotorData((int) Math.round(a + b), (int) Math.round(a -
		// b));
	}

	/*
	 * Returns a positive angle in degrees, between 0 and 360.
	 */
	private static double angleToRotate(Point<Double> current,
			Point<Double> next, double orientation) {
		Point<Double> diff = new Point<Double>(next.x - current.x,
				next.y - current.y);
		double angleToNext = Geometry.fullTanInverse(diff.x, diff.y);

		return (angleToNext - orientation + 360) % 360;
	}

}
