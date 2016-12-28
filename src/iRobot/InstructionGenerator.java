package iRobot;

public class InstructionGenerator {

	public static int STRAIGHT_VALUE = 10; // Q: What to put here?
	public static int ROTATION_VALUE = 5; // ^Same

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

		Point<Integer> nextCell = robotData.nextCell();

		// If no next cell, don't move. (i.e. if it hasn't figured out where to
		// go yet).

		if (nextCell.x == -1)
			return new MotorData(0, 0);

		Point<Integer> current = robotData.getCurrentCell();

		assert (isNeighbor(nextCell, current)); // Maybe remove this.

		Direction intendedDirection = current.directionTo(nextCell);

		System.out.println("Current cell: " + current);
		System.out.println("Next cell: " + nextCell);
		System.out.println("Orientation: " + robotData.getTrueOrientation());
		System.out.println("Intended direction: " + intendedDirection);

		if (robotData.alignedWithMainDirection()
				&& robotData.getDirectionFacing() == intendedDirection) {
			return new MotorData(STRAIGHT_VALUE, STRAIGHT_VALUE);
		} else {
			int rotatingMultiplier = directionToRotate(
					robotData.getTrueOrientation(), intendedDirection);
			// ^Either 1 or -1

			return new MotorData(ROTATION_VALUE * rotatingMultiplier * (-1),
					ROTATION_VALUE * rotatingMultiplier);
		}
	}

	private static boolean isNeighbor(Point<Integer> p1, Point<Integer> p2) {
		return (Math.abs(p1.x - p2.x) == 1 && (p1.y == p2.y))
				|| (Math.abs(p1.y - p2.y) == 1 && (p1.x == p2.x));
	}

	/*
	 * Returns 1 if needs to rotate to the left, -1 if right.
	 */
	private static int directionToRotate(double orientation, Direction dir) {
		double angleInBetween = ((dir.value - orientation) + 360) % 360;

		if (angleInBetween >= 180)
			return -1;
		else
			return 1;
	}
}
