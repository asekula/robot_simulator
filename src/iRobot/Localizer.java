package iRobot;

public class Localizer {

	// Important: Do not change this line.
	// Including this to make the code less cluttered.
	private static final double CELL_WIDTH = Constants.CELL_WIDTH;

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

		Point<Double> updated = locationInMaze;

		// ShiftAlongSensorLine relies heavily on the map.
		// It also uses Geometry.getDistanceToNearestWall.
		updated = shiftAlongSensorLine(leftDist, thetaL, updated, map);
		updated = shiftAlongSensorLine(frontDist, thetaF, updated, map);
		updated = shiftAlongSensorLine(rightDist, thetaR, updated, map);

		return updated;
	}

	private static Point<Double> shiftAlongSensorLine(double dist, double theta,
			Point<Double> location, Map map) {
		return location;
	}
}
