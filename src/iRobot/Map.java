package iRobot;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Collections;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class Map {

	private static final double OPENING_WEIGHT = 1;
	private static final double UNKNOWN_WEIGHT = 1000;

	SimpleWeightedGraph<String, DefaultWeightedEdge> stringGraph;

	// Todo: Fill this with necessary variables and methods.

	public Map(SimpleWeightedGraph<String, DefaultWeightedEdge> graph) {
		stringGraph = graph;
	}

	// Creates an graph for an unknown 16x16 maze
	public static SimpleWeightedGraph<String, DefaultWeightedEdge> UnknownMaze() {

		SimpleWeightedGraph<String, DefaultWeightedEdge> graph = new SimpleWeightedGraph<String, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);

		final int dimension = Constants.MAZE_WIDTH;

		for (int x = 0; x < dimension; x++) {
			for (int y = 0; y < dimension; y++) {
				graph.addVertex(x + "," + y);
			}
		}

		for (int x = 0; x < dimension; x++) {
			for (int y = 0; y < dimension; y++) {
				String v1 = x + "," + y;

				String v2 = (x + 1) + "," + y;
				if (graph.containsVertex(v2)) {
					if (!graph.containsEdge(v1, v2)) {
						graph.addEdge(v1, v2);
						graph.setEdgeWeight(graph.getEdge(v1, v2),
								UNKNOWN_WEIGHT);
					}
				}

				v2 = x + "," + (y + 1);
				if (graph.containsVertex(v2)) {
					if (!graph.containsEdge(v1, v2)) {
						graph.addEdge(v1, v2);
						graph.setEdgeWeight(graph.getEdge(v1, v2),
								UNKNOWN_WEIGHT);
					}
				}

			}
		}

		return graph;
	}
	/*
	 * Sets a wall between the cell and it's neighbor in the direction of dir.
	 */
	public void setWall(Point<Integer> cell, Direction dir) {

		Point<Integer> neighbor = Point.getAdjacentCell(cell, dir);
		String v1 = cell.toVertex();
		String v2 = neighbor.toVertex();

		stringGraph.removeEdge(v1, v2);
	}

	/*
	 * Sets an empty passage between the cell and it's neighbor in the input
	 * direction.
	 */
	public void setNoWall(Point<Integer> cell, Direction dir) {
		// Todo.
	}

	/*
	 * Sets a wall at the given location, where one of p's coordinates is a
	 * multiple of CELL_WIDTH.
	 */
	public void setWallAtPoint(Point<Double> p) {
		// Todo.
	}

	/*
	 * Sets a passage at the given location, where one of p's coordinates is a
	 * multiple of CELL_WIDTH.
	 */
	public void setNoWallAtPoint(Point<Double> p) {
		// Todo.
	}

	/*
	 * Returns true if there is unknown wall data pertaining the input cell.
	 */
	public boolean needsWallData(Point<Integer> cell) {
		return false;
	}

	/*
	 * Generates random maze for the emulator.
	 * 
	 * Using the pseudocode from the cs33 maze assignment:
	 * https://cs.brown.edu/courses/csci0330/docs/proj/maze.pdf
	 */
	public static SimpleWeightedGraph<String, DefaultWeightedEdge> generateRandomMaze() {

		SimpleWeightedGraph<String, DefaultWeightedEdge> graph = Map
				.UnknownMaze();

		boolean[][] visited = new boolean[Constants.MAZE_WIDTH][Constants.MAZE_WIDTH];
		// defaults to false^

		drunkenWalk(graph, visited, new Point<Integer>(0, 0));
		return graph;
	}

	/*
	 * Maintaining that row/col are within visited's bounds.
	 */
	private static void drunkenWalk(
			SimpleWeightedGraph<String, DefaultWeightedEdge> graph,
			boolean[][] visited, Point<Integer> cell) {

		String v1 = cell.toVertex();
		visited[cell.x][cell.y] = true;
		Integer[] rand = new Integer[]{0, 1, 2, 3};
		Collections.shuffle(Arrays.asList(rand));

		for (int i = 0; i < 4; i++) { // Should be random order.

			Direction dir = Direction.getDirection(rand[i] * 90);
			Point<Integer> neighbor = Point.getAdjacentCell(cell, dir);
			String v2 = neighbor.toVertex();

			if (inBounds(neighbor)) {
				if (visited[neighbor.x][neighbor.y]) {
					// If there was already a space or a wall, do nothing.
					// Else store wall.

					if (graph.containsEdge(v1, v2)) {
						if (graph
								.getEdgeWeight(graph.getEdge(v1, v2)) == 1000) {
							graph.removeEdge(v1, v2);
						}
						// Else there was an opening.
					}
					// Else there was a wall.

				} else {
					// Want to store opening.

					// Is there a better way to do this?
					if (graph.containsEdge(v1, v2)) {
						graph.removeEdge(v1, v2);
					}

					graph.addEdge(v1, v2, new DefaultWeightedEdge());
					graph.setEdgeWeight(graph.getEdge(v1, v2), OPENING_WEIGHT);
					drunkenWalk(graph, visited, neighbor);
				}
			}
		}
	}

	private static boolean inBounds(Point<Integer> cell) {
		return (cell.x < Constants.MAZE_WIDTH && cell.x >= 0
				&& cell.y < Constants.MAZE_WIDTH && cell.y >= 0);
	}

	public boolean wallAt(Point<Double> p) {
		return true;
	}

	/*
	 * Returns true if wall or unknown. (true if unknown because the
	 * localization code calculates all possibilities).
	 */
	public boolean wallBetween(Point<Integer> cell1, Point<Integer> cell2) {
		return true;
	}

	public void drawMaze(Graphics g) {
		g.setColor(Color.BLACK);
		drawMazeBounds(g);

		for (int x = 0; x < Constants.MAZE_WIDTH; x++) {
			for (int y = 0; y < Constants.MAZE_WIDTH; y++) {
				Point<Integer> cell = new Point<Integer>(x, y);
				Point<Integer> neighbor1 = Point.getAdjacentCell(cell,
						Direction.NORTH);
				Point<Integer> neighbor2 = Point.getAdjacentCell(cell,
						Direction.EAST);

				if (!stringGraph.containsEdge(cell.toVertex(),
						neighbor1.toVertex())) {
					drawWall(g, cell, Direction.NORTH);
				} else if (stringGraph
						.getEdgeWeight(stringGraph.getEdge(cell.toVertex(),
								neighbor1.toVertex())) == UNKNOWN_WEIGHT) {
					g.setColor(Color.LIGHT_GRAY);
					drawWall(g, cell, Direction.NORTH);
					g.setColor(Color.BLACK);
				}

				if (!stringGraph.containsEdge(cell.toVertex(),
						neighbor2.toVertex())) {
					drawWall(g, cell, Direction.EAST);
				} else if (stringGraph
						.getEdgeWeight(stringGraph.getEdge(cell.toVertex(),
								neighbor2.toVertex())) == UNKNOWN_WEIGHT) {
					g.setColor(Color.LIGHT_GRAY);
					drawWall(g, cell, Direction.EAST);
					g.setColor(Color.BLACK);
				}
			}
		}

		g.setColor(Color.BLACK);
	}

	private void drawMazeBounds(Graphics g) {
		int width = (int) (Constants.MAZE_WIDTH * Constants.CELL_WIDTH
				* Constants.SCALE_FACTOR);
		g.drawLine(0, 0, width, 0);
		g.drawLine(0, 0, 0, width);
		g.drawLine(0, width, width, width);
		g.drawLine(width, 0, width, width);

	}

	private void drawWall(Graphics g, Point<Integer> cell, Direction dir) {
		Point<Integer> p1, p2;
		if (dir == Direction.NORTH || dir == Direction.WEST) {
			p1 = new Point<Integer>(
					(int) (cell.x * Constants.CELL_WIDTH
							* Constants.SCALE_FACTOR),
					(int) ((cell.y + 1) * Constants.CELL_WIDTH
							* Constants.SCALE_FACTOR));
		} else {
			p1 = new Point<Integer>(
					(int) ((cell.x + 1) * Constants.CELL_WIDTH
							* Constants.SCALE_FACTOR),
					(int) (cell.y * Constants.CELL_WIDTH
							* Constants.SCALE_FACTOR));

		}

		if (dir == Direction.NORTH || dir == Direction.EAST) {
			p2 = new Point<Integer>(
					(int) ((cell.x + 1) * Constants.CELL_WIDTH
							* Constants.SCALE_FACTOR),
					(int) ((cell.y + 1) * Constants.CELL_WIDTH
							* Constants.SCALE_FACTOR));

		} else {
			p2 = new Point<Integer>(
					(int) (cell.x * Constants.CELL_WIDTH
							* Constants.SCALE_FACTOR),
					(int) (cell.y * Constants.CELL_WIDTH
							* Constants.SCALE_FACTOR));
		}

		g.drawLine(p1.x, p1.y, p2.x, p2.y);
	}
}
