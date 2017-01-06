package iRobot;

import java.util.LinkedList;

public class Explorer {

	/*
	 * Adds values (or maybe overrides values) in RobotData's path. Does not
	 * change the map. Uses the map data and the robot data to figure out where
	 * the robot should go.
	 */
	public static void modifyPath(Map map, Point<Integer> currentCell,
			LinkedList<Point<Integer>> path) {
		// Note: Only looks at robotData's current cell and path.
		// Only modifies the path.

		// Runs A*

		// Temporary, for testing purposes:
		if (currentCell.x == 0 && currentCell.y == 0 && path.isEmpty()) {
			path.add(new Point<Integer>(1, 0));
			path.add(new Point<Integer>(1, 1));
			path.add(new Point<Integer>(2, 1));
			path.add(new Point<Integer>(3, 1));
			path.add(new Point<Integer>(4, 1));
			path.add(new Point<Integer>(5, 1));
			path.add(new Point<Integer>(5, 2));
			path.add(new Point<Integer>(5, 3));
			path.add(new Point<Integer>(5, 4));
			path.add(new Point<Integer>(5, 4));
			path.add(new Point<Integer>(4, 4));
			path.add(new Point<Integer>(4, 3));
			path.add(new Point<Integer>(4, 2));
			path.add(new Point<Integer>(3, 2));
			path.add(new Point<Integer>(2, 2));
			path.add(new Point<Integer>(1, 2));
			path.add(new Point<Integer>(0, 2));
			path.add(new Point<Integer>(0, 1));
			path.add(new Point<Integer>(0, 0));
		}
	}
}
