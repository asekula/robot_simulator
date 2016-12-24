package iRobot;

public enum Direction {
	EAST(0), NORTH(1), WEST(2), SOUTH(3); // Q: Should include NONE?

	public int value;
	Direction(int value) {
		this.value = value;
	}

	public Direction left() {
		return Direction.values()[(value + 1) % 4];
	}

	public Direction right() {
		return Direction.values()[(value + 3) % 4];
	}
}
