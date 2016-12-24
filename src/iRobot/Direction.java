package iRobot;

public enum Direction {
	EAST(0), NORTH(90), WEST(180), SOUTH(270); // Q: Should include NONE?

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
}
