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

		if (stringGraph.containsVertex(v1) && stringGraph.containsVertex(v2)) {
			stringGraph.removeEdge(v1, v2);
		}
	}

	/*
	 * Sets an empty passage between the cell and it's neighbor in the input
	 * direction.
	 */
	public void setNoWall(Point<Integer> cell, Direction dir) {
		Point<Integer> neighbor = Point.getAdjacentCell(cell, dir);
		String v1 = cell.toVertex();
		String v2 = neighbor.toVertex();

		if (stringGraph.containsVertex(v1) && stringGraph.containsVertex(v2)) {
			if (!stringGraph.containsEdge(v1, v2)) {
				stringGraph.addEdge(v1, v2);
			}

			stringGraph.setEdgeWeight(stringGraph.getEdge(v1, v2),
					OPENING_WEIGHT);
		}
	}

	/*
	 * Sets a wall at the given location, where one of p's coordinates is a
	 * multiple of CELL_WIDTH.
	 * 
	 * Todo: Code reuse...when I'm not feeling lazy clean this up.
	 */
	public void setWallAtPoint(Point<Double> p) {
		if (!tooCloseToCorner(p)) {
			if (!tooCloseToCorner(p)) {
				double remainderX = p.x % Constants.CELL_WIDTH;
				double remainderY = p.y % Constants.CELL_WIDTH;
				double error = 0.01;

				if (Geometry.within(remainderX, 0, error) || Geometry
						.within(remainderX, Constants.CELL_WIDTH, error)) {

					// Important: x is round and y is floor.
					int cellX = (int) Math.round(p.x / Constants.CELL_WIDTH);
					int cellY = (int) Math.floor(p.y / Constants.CELL_WIDTH);
					Direction dir = Direction.WEST;

					Point<Integer> cell = new Point<Integer>(cellX, cellY);
					setWall(cell, dir);
				}

				if (Geometry.within(remainderY, 0, error) || Geometry
						.within(remainderY, Constants.CELL_WIDTH, error)) {

					int cellX = (int) Math.floor(p.x / Constants.CELL_WIDTH);
					int cellY = (int) Math.round(p.y / Constants.CELL_WIDTH);
					Direction dir = Direction.SOUTH;

					Point<Integer> cell = new Point<Integer>(cellX, cellY);
					setWall(cell, dir);
				}
			}
		}
		// Else do nothing.
	}

	/*
	 * Sets a passage at the given location, where one of p's coordinates is a
	 * multiple of CELL_WIDTH.
	 */
	public void setNoWallAtPoint(Point<Double> p) {
		if (!tooCloseToCorner(p)) {
			double remainderX = p.x % Constants.CELL_WIDTH;
			double remainderY = p.y % Constants.CELL_WIDTH;
			double error = 0.01;

			if (Geometry.within(remainderX, 0, error) || Geometry
					.within(remainderX, Constants.CELL_WIDTH, error)) {

				// Important: x is round and y is floor.
				int cellX = (int) Math.round(p.x / Constants.CELL_WIDTH);
				int cellY = (int) Math.floor(p.y / Constants.CELL_WIDTH);
				Direction dir = Direction.WEST;

				Point<Integer> cell = new Point<Integer>(cellX, cellY);
				setNoWall(cell, dir);
			}

			if (Geometry.within(remainderY, 0, error) || Geometry
					.within(remainderY, Constants.CELL_WIDTH, error)) {

				int cellX = (int) Math.floor(p.x / Constants.CELL_WIDTH);
				int cellY = (int) Math.round(p.y / Constants.CELL_WIDTH);
				Direction dir = Direction.SOUTH;

				Point<Integer> cell = new Point<Integer>(cellX, cellY);
				setNoWall(cell, dir);
			}
		}
		// Else do nothing.
	}

	private boolean tooCloseToCorner(Point<Double> p) {
		return (closeToBounds(p.x) && closeToBounds(p.y));
	}

	private boolean closeToBounds(double val) {
		double error = 1;
		return Geometry.within((val % Constants.CELL_WIDTH), 0, error)
				|| Geometry.within((val % Constants.CELL_WIDTH),
						Constants.CELL_WIDTH, error);
	}

	/*
	 * Returns true if there is unknown wall data pertaining the input cell.
	 * 
	 * Make this return something other than always false to test mapping. With
	 * always false, no mapping occurs.
	 */
	public boolean needsWallData(Point<Integer> cell) {
		if (stringGraph.containsVertex(cell.toVertex())) {
			for (DefaultWeightedEdge e : stringGraph.edgesOf(cell.toVertex())) {
				if (stringGraph.getEdgeWeight(e) == UNKNOWN_WEIGHT) {
					return true;
				}
			}
		}
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
	 * Maintaining that cell is within visited's bounds.
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

	// For sensors. Returns false if unknown.
	// Should not be used by robot.
	public boolean wallAt(Point<Double> p) {
		double remainderX = p.x % Constants.CELL_WIDTH;
		double remainderY = p.y % Constants.CELL_WIDTH;
		double error = 0.01;

		if (Geometry.within(remainderX, 0, error)
				|| Geometry.within(remainderX, Constants.CELL_WIDTH, error)) {

			// Important: x is round and y is floor.
			int cellX = (int) Math.round(p.x / Constants.CELL_WIDTH);
			int cellY = (int) Math.floor(p.y / Constants.CELL_WIDTH);
			Direction dir = Direction.WEST;

			Point<Integer> cell = new Point<Integer>(cellX, cellY);
			Point<Integer> neighbor = Point.getAdjacentCell(cell, dir);

			return !stringGraph.containsEdge(cell.toVertex(),
					neighbor.toVertex());
		}

		if (Geometry.within(remainderY, 0, error)
				|| Geometry.within(remainderY, Constants.CELL_WIDTH, error)) {

			int cellX = (int) Math.floor(p.x / Constants.CELL_WIDTH);
			int cellY = (int) Math.round(p.y / Constants.CELL_WIDTH);
			Direction dir = Direction.SOUTH;

			Point<Integer> cell = new Point<Integer>(cellX, cellY);
			Point<Integer> neighbor = Point.getAdjacentCell(cell, dir);

			return !stringGraph.containsEdge(cell.toVertex(),
					neighbor.toVertex());
		}

		return false;
	}

	/*
	 * Returns true if wall or unknown. (true if unknown because the
	 * localization code calculates all possibilities).
	 */
	public boolean wallBetween(Point<Integer> cell1, Point<Integer> cell2) {
		if (stringGraph.containsEdge(cell1.toVertex(), cell2.toVertex())) {
			if (stringGraph.getEdgeWeight(stringGraph.getEdge(cell1.toVertex(),
					cell2.toVertex())) == UNKNOWN_WEIGHT) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	/*
	 * Assuming there are no unknown edges in the graph.
	 */
	public void drawTrueMaze(Graphics g) {
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
					drawWall(g, cell, Direction.NORTH, 0);
				}

				if (!stringGraph.containsEdge(cell.toVertex(),
						neighbor2.toVertex())) {
					drawWall(g, cell, Direction.EAST, 0);
				}
			}
		}

		g.setColor(Color.BLACK);
	}

	// Coloring scheme:
	// Unknown - light grey
	// Perceived wall that is a true wall - green
	// Perceived wall that is not a true wall - orange
	// Perceived opening that is a true wall - red
	// Perceived opening that is a true opening - no color
	public void drawRobotMap(Graphics g, Map trueMap) {
		int pixelOffset = 2;
		g.setColor(Color.GREEN);
		drawMazeBounds(g);

		for (int x = 0; x < Constants.MAZE_WIDTH; x++) {
			for (int y = 0; y < Constants.MAZE_WIDTH; y++) {
				Point<Integer> cell = new Point<Integer>(x, y);
				Point<Integer> neighbor1 = Point.getAdjacentCell(cell,
						Direction.NORTH);
				Point<Integer> neighbor2 = Point.getAdjacentCell(cell,
						Direction.EAST);

				// No edge == wall.
				if (!stringGraph.containsEdge(cell.toVertex(),
						neighbor1.toVertex())) {
					if (trueMap.stringGraph.containsEdge(cell.toVertex(),
							neighbor1.toVertex())) { // If there's actually an
														// opening.
						g.setColor(Color.ORANGE);
					} else {
						g.setColor(Color.GREEN);
					}
					drawWall(g, cell, Direction.NORTH, pixelOffset);
				} else if (stringGraph
						.getEdgeWeight(stringGraph.getEdge(cell.toVertex(),
								neighbor1.toVertex())) == UNKNOWN_WEIGHT) {
					// If unknown.
					g.setColor(Color.WHITE);
					drawWall(g, cell, Direction.NORTH, pixelOffset);
				} else {
					// If the true map has a wall there...
					if (!trueMap.stringGraph.containsEdge(cell.toVertex(),
							neighbor1.toVertex())) {
						g.setColor(Color.RED);
						System.out.println("\t\tRED");
						drawWall(g, cell, Direction.NORTH, pixelOffset);
					}
				}

				if (!stringGraph.containsEdge(cell.toVertex(),
						neighbor2.toVertex())) {
					if (trueMap.stringGraph.containsEdge(cell.toVertex(),
							neighbor2.toVertex())) { // If there's actually an
														// opening.
						g.setColor(Color.ORANGE);
					} else {
						g.setColor(Color.GREEN);
					}
					drawWall(g, cell, Direction.EAST, pixelOffset);
				} else if (stringGraph
						.getEdgeWeight(stringGraph.getEdge(cell.toVertex(),
								neighbor2.toVertex())) == UNKNOWN_WEIGHT) {
					g.setColor(Color.WHITE);
					drawWall(g, cell, Direction.EAST, pixelOffset);
				} else {
					// If the true map has a wall there...
					if (!trueMap.stringGraph.containsEdge(cell.toVertex(),
							neighbor2.toVertex())) {
						g.setColor(Color.RED);
						System.out.println("\t\tRED");
						drawWall(g, cell, Direction.EAST, pixelOffset);
					}
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

	private void drawWall(Graphics g, Point<Integer> cell, Direction dir,
			int offset) {
		Point<Integer> p1, p2;
		if (dir == Direction.NORTH || dir == Direction.WEST) {
			p1 = new Point<Integer>(
					(int) (cell.x * Constants.CELL_WIDTH
							* Constants.SCALE_FACTOR) + offset,
					(int) ((cell.y + 1) * Constants.CELL_WIDTH
							* Constants.SCALE_FACTOR) + offset);
		} else {
			p1 = new Point<Integer>(
					(int) ((cell.x + 1) * Constants.CELL_WIDTH
							* Constants.SCALE_FACTOR) + offset,
					(int) (cell.y * Constants.CELL_WIDTH
							* Constants.SCALE_FACTOR) + offset);

		}

		if (dir == Direction.NORTH || dir == Direction.EAST) {
			p2 = new Point<Integer>(
					(int) ((cell.x + 1) * Constants.CELL_WIDTH
							* Constants.SCALE_FACTOR) + offset,
					(int) ((cell.y + 1) * Constants.CELL_WIDTH
							* Constants.SCALE_FACTOR) + offset);

		} else {
			p2 = new Point<Integer>(
					(int) (cell.x * Constants.CELL_WIDTH
							* Constants.SCALE_FACTOR) + offset,
					(int) (cell.y * Constants.CELL_WIDTH
							* Constants.SCALE_FACTOR) + offset);
		}

		g.drawLine(p1.x, p1.y, p2.x, p2.y);
	}
}
