package iRobot;

import java.util.LinkedList;

public class Explorer {

	/*
	 * Adds values (or maybe overrides values) in RobotData's path. Does not
	 * change the map. Uses the map data and the robot data to figure out where
	 * the robot should go.
	 */
	public static void modifyPath(Map map, Point<Integer> currentCell,
			LinkedList<Point<Integer>> path, LinkedList<Point<Integer>> traversedPath) {
		// Note: Only looks at robotData's current cell and path.
		// Only modifies the path.

	  //Maps the Maze using DFS
	  // Alternative which we can also try - A* 

	  
	  //Waits for the robot to collect the data first before giving more cells
	    if(!path.isEmpty() || 
	         map.needsWallData(currentCell)) {
	      return;
	    }
	  
	  
	    Direction dir = Direction.EAST;
	    
	    for(int i=1; i<=4; i++) {
	      Point<Integer> adjacentCell = Point.getAdjacentCell(currentCell, dir);
	      
	    if(map.needsWallData(adjacentCell) &&
	        !map.wallBetween(currentCell, adjacentCell)) {
	      path.add(adjacentCell);
	      traversedPath.add(adjacentCell);
	      System.out.println("Added "+adjacentCell.toVertex());
	      return;
	    }
	    dir = dir.left();
	    }
	    
	    System.out.println(traversedPath.toString());
	    
	    //backtracking
	    for(int i = traversedPath.size()-1; i>=0; i--) {
	      path.add(traversedPath.get(i));
	      if(map.needsWallData(traversedPath.get(i))) {
	        break;
	      }
	    }
	
	}
}
