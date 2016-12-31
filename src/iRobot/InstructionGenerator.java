package iRobot;

public class InstructionGenerator {

	public static int STRAIGHT_VALUE = 10; // Q: What to put here?
	public static int ROTATION_VALUE = 2; // ^Same

	public static MotorData generateMotorData(RobotData robotData) {
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

		if (next.x == -1)
			return new MotorData(0, 0);

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
