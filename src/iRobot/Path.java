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
	 */
	public Point<Integer> getNextCell() {
		return new Point<Integer>(0, 0);
	}

	/*
	 * Called when it reaches the head. Removes it from the path.
	 */
	public void removeHead() {

	}
}
