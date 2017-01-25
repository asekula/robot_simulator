package iRobot;

public class Localizer {

	private static double CELL_WIDTH = Constants.CELL_WIDTH;

	/*
	 * This method uses the robot's data (including sensor data) to update the
	 * location of the robot. It is assumed that the robot already moved itself
	 * via curveRobot with the tacho counts.
	 * 
	 * Orientation is the true orientation of the robot. This method uses only
	 * the sensor data to update the robot's location.
	 * 
	 * The number of coordinates we can update (0, 1, or 2) depends on which
	 * values are -1, and which grid line types (horizontal or vertical) the
	 * sensors are detecting.
	 * 
	 * This method is meant to update the small errors that arise from
	 * curveRobot. If there is enough data, then it won't even use the input
	 * location, and return the true location (e.g. when all sensors return
	 * values != -1).
	 * 
	 * Returns a fixed location. Does not modify anything.
	 * 
	 * Only public interface of Localizer.
	 */
	public static Point<Double> fixedLocation(Point<Double> locationInMaze,
			SensorData sensorData, Map map, double orientation) {

		double leftDist = -1.0, rightDist = -1.0, frontDist = -1.0;

		// When making up for the fact that the L/R sensors aren't
		// directly over the motors, here's where to do it.

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

		// If we need to save memory, here's a good place to do it.
		double thetaL = (90 + orientation) % 360, thetaF = orientation,
				thetaR = (270 + orientation) % 360;

		Point<Double> p1, p2, p3;

		// ShiftAlongSensorLine relies heavily on the map.
		// It also uses Geometry.getDistanceToNearestWall.
		p1 = shiftAlongSensorLine(frontDist, thetaF, locationInMaze, map);
		p2 = shiftAlongSensorLine(leftDist, thetaL, locationInMaze, map);
		p3 = shiftAlongSensorLine(rightDist, thetaR, locationInMaze, map);

		Point<Double> avg = avgData(locationInMaze, p1, p2, p3);
		if (Geometry.within(Geometry.distanceBetween(avg, locationInMaze), 0,
				Constants.LOCALIZATION_ERROR)) {
			return avg;
		} else {
			return locationInMaze;
		}
	}

	private static Point<Double> shiftAlongSensorLine(double realDist,
			double theta, Point<Double> location, Map map) {

		Point<Double> unknown = new Point<Double>(-CELL_WIDTH, -CELL_WIDTH);

		if (realDist == -1)
			return unknown;

		double maxDist = Math.min(Constants.FRONT_IR_TO_CENTER,
				Constants.DISTANCE_BETWEEN_MOTORS / 2) + Constants.IR_MAX;

		double dist = Geometry.getDistanceToNearestWall(location, theta,
				maxDist, map);

		if (dist == -1)
			return unknown;

		/*
		 * For now, only using a sensor value to correct location if it's close
		 * enough to the perceived sensor value. (if one is -1 and the other
		 * returns a value), or if they're far apart, then we won't bother
		 * figuring out where it should be (for now).
		 */

		if (!Geometry.within(realDist, dist, Constants.MAX_SHIFT_DISTANCE))
			return unknown;

		return Geometry.getRelativePoint(location, 0, theta, dist - realDist);
	}

	/*
	 * Averages values when they are not -CELL_WIDTH. Returns a point containing
	 * those average values.
	 */
	private static Point<Double> avgData(Point<Double> location,
			Point<Double> p1, Point<Double> p2, Point<Double> p3) {
		int xCoords = 0, yCoords = 0;
		Point<Double> avgPoint = new Point<Double>(0.0, 0.0);

		// Want tuple here.
		xCoords = addToAvg(avgPoint, p1.x, xCoords, true);
		xCoords = addToAvg(avgPoint, p2.x, xCoords, true);
		xCoords = addToAvg(avgPoint, p3.x, xCoords, true);

		yCoords = addToAvg(avgPoint, p1.y, yCoords, false);
		yCoords = addToAvg(avgPoint, p2.y, yCoords, false);
		yCoords = addToAvg(avgPoint, p3.y, yCoords, false);

		if (xCoords == 0) {
			avgPoint.x = location.x;
		} else {
			avgPoint.x /= xCoords;
		}

		if (yCoords == 0) {
			avgPoint.y = location.y;
		} else {
			avgPoint.y /= yCoords;
		}

		return avgPoint;
	}

	/*
	 * Returns num if avg wasn't added to, returns num + 1 if it added val to
	 * one of avg's coordinates (as decided by xPos).
	 */
	private static int addToAvg(Point<Double> avg, double val, int num,
			boolean xPos) {

		if (val != -Constants.CELL_WIDTH) {
			if (xPos) {
				avg.x += val;
			} else {
				avg.y += val;
			}
			return num + 1;
		} else {
			return num;
		}
	}
}
