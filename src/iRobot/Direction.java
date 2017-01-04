package iRobot;

public enum Direction {
	EAST(0), NORTH(90), WEST(180), SOUTH(270); // Q: Should include NONE? -
												// We can do unknown?

	public int value;
	Direction(int value) {
		this.value = value;
	}

	public Direction left() {
		return Direction.values()[((value / 90) + 1) % 4];
	}

	public Direction right() {
		return Direction.values()[((value / 90) + 3) % 4];
	}

	// Assuming that orientation is one of 0, 90, 180, 270.
	public static Direction getDirection(double orientation) {
		if (orientation == 0) {
			return Direction.EAST;
		} else if (orientation == 90) {
			return Direction.NORTH;
		} else if (orientation == 180) {
			return Direction.WEST;
		} else {
			return Direction.SOUTH;
		}
	}

	public static boolean isMainDirection(double orientation) {
		return (orientation == 0 || orientation == 90 || orientation == 180
				|| orientation == 270);
	}
}
