package iRobot;

public class FastTester {

	public static void main(String[] args) {
		int numTests = 200;
		int succeeded = 0;

		for (int i = 0; i < numTests; i++) {
			if (runTest()) {
				succeeded++;
				System.out.println("Passed.");
			} else {
				System.out.println("Failed.");
			}
		}

		System.out.println(succeeded + " out of " + numTests + " passed.");
		System.out.println("Performance: " + (((double) succeeded) / numTests));
	}

	/*
	 * Returns true if the test passed, false if not.
	 */
	private static boolean runTest() {
		Emulator emulator;
		DataBuffer buffer;
		Brain brain;
		RobotData robotData;
		SensorData sensorData;
		MotorData motorData;

		emulator = new Emulator();
		buffer = new DataBuffer(emulator);
		robotData = buffer.calibrate();
		brain = new Brain(robotData);

		emulator.moveRobot();

		do {
			sensorData = buffer.getSensorData();
			motorData = brain.computeMotorData(sensorData);
			buffer.moveRobotMotors(motorData);
			emulator.moveRobot();

			if (containsError(brain.getMap(), emulator.getMap())) {
				return false;
			}
		} while (brain.getMap().needsWallData());

		return true;
	}

	private static boolean containsError(Map map1, Map map2) {
		for (int i = 0; i < Constants.MAZE_WIDTH; i++) {
			for (int j = 0; j < Constants.MAZE_WIDTH; j++) {
				try {
					if (!sameWalls(map1, map2, new Point<Integer>(i, j))) {
						return true;
					}
				} catch (Exception e) {
					continue;
				}
			}
		}
		return false;
	}

	private static boolean sameWalls(Map map1, Map map2, Point<Integer> cell) {
		Direction dir = Direction.EAST;
		Point<Integer> neighbor;

		for (int i = 0; i < 4; i++) {
			neighbor = Point.getAdjacentCell(cell, dir);
			double weight1 = -1, weight2 = -1; // -1 is no edge.

			if (map1.stringGraph.containsEdge(cell.toVertex(),
					neighbor.toVertex())) {
				weight1 = map1.stringGraph.getEdgeWeight(map1.stringGraph
						.getEdge(cell.toVertex(), neighbor.toVertex()));
			}

			if (map2.stringGraph.containsEdge(cell.toVertex(),
					neighbor.toVertex())) {
				weight2 = map2.stringGraph.getEdgeWeight(map2.stringGraph
						.getEdge(cell.toVertex(), neighbor.toVertex()));
			}

			if (decidedWeight(weight1) && decidedWeight(weight2)) {
				if (weight1 != weight2) {
					System.out.println("Cell: " + cell);
					System.out.println("Direction: " + dir);
					System.out.println("Weight1: " + weight1);
					System.out.println("Weight2: " + weight2);
					return false;
				}
			}

			dir = dir.left();
		}

		return true;
	}

	private static boolean decidedWeight(double weight) {
		return (weight == Map.OPENING_WEIGHT || weight == -1);
	}
}
