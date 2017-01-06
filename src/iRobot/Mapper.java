package iRobot;

public class Mapper {

	/*
	 * Once again, a lot of opportunities to save computation/memory in here.
	 */

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
		 * Will only do mapping if it's within a certain area in the current
		 * cell, to avoid edge cases where it's moving from one cell to another
		 * and it detects a cell change but the sensors still see the previous
		 * wall.
		 */

		if (insideCell(robotData.getLocationInMaze())
				&& map.needsWallData(robotData.getCurrentCell())) {

			Point<Double> loc = robotData.getLocationInMaze();
			Point<Integer> cell = robotData.getCurrentCell();

			double orientation = robotData.getTrueOrientation();
			double thetaL = (orientation + 90) % 360;
			double thetaR = (orientation + 270) % 360;
			double leftDist = -1.0, rightDist = -1.0, frontDist = -1.0;

			if (sensorData.leftIR != -1) {
				leftDist = sensorData.leftIR
						+ (Constants.DISTANCE_BETWEEN_MOTORS / 2);
			}
			if (sensorData.rightIR != -1) {
				rightDist = sensorData.rightIR
						+ (Constants.DISTANCE_BETWEEN_MOTORS / 2);
			}
			if (sensorData.frontIR != -1) {
				frontDist = sensorData.frontIR + Constants.FRONT_IR_TO_CENTER;
			}

			updateMapWithSensor(map, leftDist, thetaL, loc, cell);
			updateMapWithSensor(map, frontDist, orientation, loc, cell);
			updateMapWithSensor(map, rightDist, thetaR, loc, cell);
		}
	}

	/*
	 * Use a single sensor to figure out if there is a wall or not ahead. Can
	 * determine a lot from a single sensor value.
	 */
	private static void updateMapWithSensor(Map map, double dist, double theta,
			Point<Double> location, Point<Integer> cell) {

		if (Direction.isMainDirection(theta)) {
			Direction dir = Direction.getDirection(theta);
			updateMapWithDirection(map, dist, dir, location, cell);
		}

		Point<Double> xGridLinePoint = Geometry.getXGridLinePoint(location,
				theta);
		Point<Double> yGridLinePoint = Geometry.getYGridLinePoint(location,
				theta);

		double xGridLineDist = Geometry.distanceBetween(location,
				xGridLinePoint);
		double yGridLineDist = Geometry.distanceBetween(location,
				yGridLinePoint);

		double error = 0.1;

		if (Geometry.within(xGridLineDist, yGridLineDist, error)) {
			return; // Too close to decide walls.
		}

		if (dist == -1) {
			if (xGridLineDist <= Constants.IR_MAX) {
				setWallXGridLine(map, location, theta, false);
			}

			if (yGridLineDist <= Constants.IR_MAX) {
				setWallYGridLine(map, location, theta, false);
			}
		} else { // A wall was detected.
			if (xGridLineDist < yGridLineDist) {
				if (Geometry.within(xGridLineDist, dist, error)) {
					setWallXGridLine(map, location, theta, true);
				} else if (Geometry.within(yGridLineDist, dist, error)) {
					setWallXGridLine(map, location, theta, false);
					setWallYGridLine(map, location, theta, true);
				} else {
					assert (false);
					// ^Because one of the two grid lines were detected.
				}
			} else {
				if (Geometry.within(yGridLineDist, dist, error)) {
					setWallYGridLine(map, location, theta, true);
				} else if (Geometry.within(xGridLineDist, dist, error)) {
					setWallYGridLine(map, location, theta, false);
					setWallXGridLine(map, location, theta, true);
				} else {
					assert (false);
				}
			}
		}
	}

	private static void setWallXGridLine(Map map, Point<Double> loc,
			double theta, boolean setWall) {

		Point<Double> wallPoint = new Point<Double>(
				Geometry.getXGridLine(loc.x, theta), loc.y);

		if (setWall) {
			map.setWallAtPoint(wallPoint);
		} else {
			map.setWallAtPoint(wallPoint);
		}
	}

	private static void setWallYGridLine(Map map, Point<Double> loc,
			double theta, boolean setWall) {

		Point<Double> wallPoint = new Point<Double>(loc.x,
				Geometry.getYGridLine(loc.y, theta));

		if (setWall) {
			map.setWallAtPoint(wallPoint);
		} else {
			map.setNoWallAtPoint(wallPoint);
		}
	}

	/*
	 * The sensor is aligned with one of the four main directions.
	 */
	private static void updateMapWithDirection(Map map, double distance,
			Direction dir, Point<Double> location, Point<Integer> cell) {
		if (distance != -1) {

			// If it's not detecting the next cell's wall.
			if (distance - Math.max(Constants.FRONT_IR_TO_CENTER,
					Constants.DISTANCE_BETWEEN_MOTORS
							/ 2) < Constants.CELL_WIDTH) {

				map.setWall(cell, dir);

			}
			// else: Won't bother, really small edge case and we don't need this
			// functionality.
		} else {
			map.setNoWall(cell, dir);
		}
	}

	private static boolean insideCell(Point<Double> p) {
		double dist = 0.5;
		double x = p.x % Constants.CELL_WIDTH;
		double y = p.y % Constants.CELL_WIDTH;
		return ((x <= Constants.CELL_WIDTH - dist) && (x >= dist)
				&& (y <= Constants.CELL_WIDTH - dist) && (y >= dist));
	}
}
