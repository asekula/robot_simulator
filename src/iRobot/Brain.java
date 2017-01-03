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

		if (robotData.getPhase() == Phase.EXPLORING) {

			/*
			 * Important: Updating the location after mapping assumes that the
			 * location denoted by curveRobot is roughly accurate, and that the
			 * true location isn't far off if it started out correct before
			 * curveRobot was called. This requires that there isn't much wait
			 * time between iterations.
			 * 
			 * The reason behind
			 */

			robotData.updateData(sensorData); // Curves robot.
			Mapper.updateMap(sensorData, robotData, map);
			robotData.fixLocation(sensorData, map); // Uses sensors to fix the
													// location.

			// Mapper alters the map according to the sensorData and robotData.
			// Explicitly modifying the current map object to save memory.

			Explorer.modifyPath(map, robotData.getCurrentCell(),
					robotData.getPath());
		} else {
			robotData.updateData(sensorData);
			robotData.fixLocation(sensorData, map);

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
