package iRobot;

import java.util.LinkedList;
import java.util.List;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Explorer {

	/*
	 * Adds values (or maybe overrides values) in RobotData's path. Does not
	 * change the map. Uses the map data and the robot data to figure out where
	 * the robot should go.
	 */
	public static void modifyPath(Map map, Point<Integer> currentCell,
			LinkedList<Point<Integer>> path,
			LinkedList<Point<Integer>> traversedPath) {
		// Note: Only looks at robotData's current cell and path.
		// Only modifies the path.

		// Maps the Maze using DFS
		// Alternative which we can also try - A*

		// Waits for the robot to collect the data first before giving more
		// cells

		if (!path.isEmpty() || map.needsWallData(currentCell)) {
			return;
		}

		Direction dir = Direction.EAST;

		for (int i = 1; i <= 4; i++) {
			Point<Integer> adjacentCell = Point.getAdjacentCell(currentCell,
					dir);

			if (map.needsWallData(adjacentCell)
					&& !map.wallBetween(currentCell, adjacentCell)) {
				path.add(adjacentCell);
				traversedPath.add(adjacentCell);
				return;
			}
			dir = dir.left();
		}

		// Path is empty at this point.

		List<DefaultWeightedEdge> bestPath = null;

		for (int i = 0; i < traversedPath.size(); i++) {
			Point<Integer> lastTraversed = traversedPath.get(i);
			if (accessibleNeighborNeedsWallData(map, lastTraversed)) {

				// Shouldn't redo computation every time.
				List<DefaultWeightedEdge> shortest = DijkstraShortestPath
						.findPathBetween(map.stringGraph,
								currentCell.toVertex(),
								lastTraversed.toVertex());

				if (bestPath != null) {
					if (shortest.size() < bestPath.size()) {
						bestPath = shortest;
					}
				} else {
					bestPath = shortest;
				}
			}
		}

		if (bestPath != null) {
			Solver.convertPath(map, bestPath, path, currentCell);
			traversedPath.removeAll(path);
		} else {
			if (map.needsWallData()) {
				System.out.println("Todo: This case.");
			}
		}
	}

	/*
	 * If an accessible neighbor of the input cell needs wall data.
	 */
	private static boolean accessibleNeighborNeedsWallData(Map map,
			Point<Integer> cell) {
		Direction dir = Direction.EAST;
		for (int i = 1; i <= 4; i++) {
			Point<Integer> adjacentCell = Point.getAdjacentCell(cell, dir);
			if (map.needsWallData(adjacentCell)
					&& !map.wallBetween(cell, adjacentCell)) {
				return true;
			}
			dir = dir.left();
		}
		return false;
	}
}
