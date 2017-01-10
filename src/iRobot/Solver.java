package iRobot;

import java.util.LinkedList;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.GraphPath;

public class Solver {

	/*
	 * Adds to the robotData's path if it hasn't already found the path to it's
	 * goals.
	 */
	public static void modifyPath(Map map, Point<Integer> currentCell,
			Point<Integer> goalCell, LinkedList<Point<Integer>> path) {
		// Runs Dijkstra's.

		if (path.isEmpty()) {
			DijkstraShortestPath<String, DefaultWeightedEdge> pathFinder = new DijkstraShortestPath<String, DefaultWeightedEdge>(
					map.stringGraph, currentCell.toVertex(),
					goalCell.toVertex());

			convertPath(map, pathFinder.getPath(), path, currentCell);
		}
	}

	private static void convertPath(Map map,
			GraphPath<String, DefaultWeightedEdge> graphPath,
			LinkedList<Point<Integer>> path, Point<Integer> currentCell) {
		Point<Integer> current = currentCell;

		for (DefaultWeightedEdge e : graphPath.getEdgeList()) {
			Point<Integer> next = otherVertex(map, current, e);
			path.add(next);
			current = next;
		}
	}

	private static Point<Integer> otherVertex(Map map, Point<Integer> vertex,
			DefaultWeightedEdge e) {

		if (vertex.toVertex().equals(map.stringGraph.getEdgeSource(e))) {
			return toPoint(map.stringGraph.getEdgeTarget(e));
		} else {
			return toPoint(map.stringGraph.getEdgeSource(e));
		}
	}

	private static Point<Integer> toPoint(String vertex) {
		String[] parts = vertex.split(",");
		return new Point<Integer>(Integer.parseInt(parts[0]),
				Integer.parseInt(parts[1]));
	}
}
