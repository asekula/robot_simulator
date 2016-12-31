package iRobot;

import java.util.LinkedList;

public class Path {

	/*
	 * Todo: Fill in necessary variables/methods. Implementation will largely
	 * depend on Map's implementation.
	 */
	private LinkedList<Point<Integer>> path;

	/*
	 * Equivalent to .head(). Returns the front of the path.
	 * 
	 * When this is actually implemented, it won't take any arguments.
	 */
	public Point<Integer> getNextCell(Point<Integer> currentCell) {

		// Temporary, for testing purposes:
		if (currentCell.x == 0 && currentCell.y == 0) {
			return new Point<Integer>(1, 0);
		}

		if (currentCell.x == 1 && currentCell.y == 0) {
			return new Point<Integer>(1, 1);
		}

		if (currentCell.x == 1 && currentCell.y == 1) {
			return new Point<Integer>(2, 1);
		}

		if (currentCell.x == 2 && currentCell.y == 1) {
			return new Point<Integer>(2, 2);
		}

		if (currentCell.x == 2 && currentCell.y == 2) {
			return new Point<Integer>(1, 2);
		}

		if (currentCell.x == 1 && currentCell.y == 2) {
			return new Point<Integer>(0, 2);
		}

		if (currentCell.x == 0 && currentCell.y == 2) {
			return new Point<Integer>(0, 1);
		}

		if (currentCell.x == 0 && currentCell.y == 1) {
			return new Point<Integer>(0, 0);
		}

		if (path == null) {
			return new Point<Integer>(-1, -1);
		}
		return new Point<Integer>(-1, -1);
	}

	/*
	 * Called when it reaches the head. Removes it from the path.
	 */
	public void removeHead() {

	}
}
