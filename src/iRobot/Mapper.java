package iRobot;

public class Mapper {

	/*
	 * Only public interface for Mapper. Important: Should only modify map's
	 * information. Shouldn't modify robotData.
	 * 
	 * Uses the front IR, the orientation, the current tile, and the local motor
	 * locations to figure out if there is a wall straight ahead.
	 */
	public static void updateMap(SensorData sensorData, RobotData robotData,
			Map map) {
		/*
		 * Note: Only doing any mapping if it is aligned with one of the four
		 * main directions.
		 */

		/*
		 * Should probably only do mapping if it's within a certain area in the
		 * current cell, to avoid edge cases where it's moving from one cell to
		 * another and it detects a cell change but the sensors still see the
		 * previous wall.
		 * 
		 * Todo: Check if the robot locationInCell is within a smaller square
		 * inside the current cell, and if so, then perform mapping.
		 */

	}
}
