package iRobot;

import java.util.Iterator;
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

				// There's probably a better way of addressing this one special
				// case...

				System.out.println("Special case.");
				List<DefaultWeightedEdge> shortest = DijkstraShortestPath
						.findPathBetween(map.stringGraph,
								currentCell.toVertex(),
								map.someCellThatNeedsData().toVertex());
				// Guaranteed that a cell needs data.

				Solver.convertPath(map, shortest, path, currentCell);
				removeUnknownConnections(map, path);

				traversedPath.removeAll(path);
				System.out.println("Problem solved: " + (!path.isEmpty()));

			}
		}
	}

	private static void removeUnknownConnections(Map map,
			LinkedList<Point<Integer>> path) {

		LinkedList<Point<Integer>> fixed = new LinkedList<Point<Integer>>();
		if (path.size() >= 2) {
			fixed.add(path.getFirst());
			Iterator<Point<Integer>> iter = path.iterator();
			Point<Integer> current = iter.next();
			while (iter.hasNext()) {
				Point<Integer> next = iter.next();
				if (map.stringGraph.containsEdge(current.toVertex(),
						next.toVertex())) {
					if (map.stringGraph.getEdgeWeight(
							map.stringGraph.getEdge(current.toVertex(),
									next.toVertex())) != Map.OPENING_WEIGHT) {
						break;
					} else {
						fixed.add(next);
					}
				} else {
					break;
				}

				current = next;
			}
		}

		path.clear();
		path.addAll(fixed);
		// There's probably a more memory-conscious way of doing that.
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
