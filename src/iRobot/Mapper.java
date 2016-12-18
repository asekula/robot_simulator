package iRobot;

public class Mapper {

	/*
	 * Only public interface for Mapper. Important: Should only modify map's
	 * information. Shouldn't modify robotData.
	 */
	public static void updateMap(SensorData sensorData, RobotData robotData,
			Map map) {
		/*
		 * Step 1: Use the front IR, the orientation, the current tile, and the
		 * local motor locations to figure out if there is a wall straight
		 * ahead.
		 */

		/*
		 * Step 2: Use the left/right IR data with the orientation, current
		 * tile, and local motor location to figure out if there is a wall on
		 * it's sides (note that it may not be able to figure that out.
		 */
	}
}
