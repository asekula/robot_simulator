package iRobot;

// Basically a tuple. Thanks java.
public class Point<T extends Comparable<T>> {
	public T x;
	public T y;

	public Point(T x, T y) {
		this.x = x;
		this.y = y;
	}

	public boolean equals(Point<T> p) {
		return (p.x.equals(x) && p.y.equals(y));
	}

	/*
	 * Assumes p is a neighbor. Really intended for ints.
	 */
	public Direction directionTo(Point<T> p) {
		if (p.x.compareTo(x) == 0) {
			if (p.y.compareTo(y) > 0) {
				return Direction.NORTH;
			} else {
				return Direction.SOUTH;
			}
		} else if (p.x.compareTo(x) > 0) {
			return Direction.EAST;
		} else {
			return Direction.WEST;
		}

		// Todo: Unit test this method. Not 100% sure if the compareTo's are
		// correct.
	}

	public String toString() {
		return "(" + x.toString() + ", " + y.toString() + ")";
	}

	public String toVertex() {
		return x.toString() + "," + y.toString();
	}

	/*
	 * Moves the current cell to one of its neighboring cells based on the
	 * direction.
	 */
	public static Point<Integer> getAdjacentCell(Point<Integer> cell,
			Direction dir) {
		Point<Integer> adjacent = new Point<Integer>(cell.x, cell.y);
		switch (dir) {
			case NORTH :
				adjacent.y += 1;
				break;
			case SOUTH :
				adjacent.y -= 1;
				break;
			case EAST :
				adjacent.x += 1;
				break;
			case WEST :
				adjacent.x -= 1;
				break;
		}

		return adjacent;
	}
}
