package iRobot;

/*
 * Where the main computation happens. Contains instances of the map, and the
 * robotData, and uses the mapper, the explorer/solver, and the instruction
 * generator.
 */
public class Brain {

	/*
	 * robotData: Represents the data that the robot has about itself, e.g.
	 * location in the maze, orientation, and which phase it is in (exploring
	 * vs. speed run). It also contains the path that it is currently doing,
	 * which gets updated by the solver/explorer.
	 */
	private RobotData robotData;
	private Map map;

	public Brain(RobotData robotData) {
		this.robotData = robotData;
		map = new Map();
	}

	/*
	 * Uses sensorData to generate MotorData. Updates the robotData, the map,
	 * and modifies the robotData's path if needed.
	 */
	public MotorData computeMotorData(SensorData sensorData) {

		/*
		 * Alters the location and orientation of the robot. Also changes it's
		 * phase value (exploring vs. speed run) if it reached its goal. Also
		 * removes the head of the path if the robot reached the head's
		 * location.
		 * 
		 * Important: updateData changes the phases.
		 */
		robotData.updateData(sensorData); // Do this in two parts. before mapper
											// + after mapper.

		if (robotData.getPhase() == Phase.EXPLORING) {
			Mapper.updateMap(sensorData, robotData, map);
			// Mapper alters the map according to the sensorData and robotData.
			// Explicitly modifying the current map object to save memory.

			Explorer.modifyPath(map, robotData);
		} else {
			Solver.modifyPath(map, robotData); // Does nothing if it already
												// found a path.
			/*
			 * Q: Should we even include this? We could alternatively compute
			 * the solution right after it changes phases in updateData, but
			 * here it's more explicit.
			 */
		}

		/*
		 * Uses robotData's path to generate motor instructions. Assumes that
		 * the path is a valid path in map, which is why it doesn't need a
		 * reference to the map.
		 */
		return InstructionGenerator.generateMotorData(robotData);
	}

	public boolean isFinished() {
		return (false); // Todo: Figure out what to do here.
		// Q: Do we want to stop the robot ever?
	}
}
