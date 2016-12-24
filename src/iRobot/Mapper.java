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

		if (robotData.alignedWithMainDirection()
				&& map.needsWallData(robotData.getCurrentCell())) {
			Direction facing = robotData.getDirectionFacing();
			Direction left = facing.left(), right = facing.right();
			Point<Integer> current = robotData.getCurrentCell();

			if (sensorData.leftIR == 1) {
				map.setWall(current, left);
			} else if (sensorData.leftIR == 0) {
				map.setNoWall(current, left);
			}

			if (sensorData.rightIR == 1) {
				map.setWall(current, right);
			} else if (sensorData.rightIR == 0) {
				map.setNoWall(current, right);
			}

			if (sensorData.frontIR != -1) {
				map.setWall(current, facing);
			} else {
				map.setNoWall(current, facing);
			}
		}
	}
}
