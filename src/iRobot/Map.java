package iRobot;

import java.awt.Color;
import java.awt.Graphics;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;


public class Map {
  
  UndirectedGraph<String, DefaultWeightedEdge> stringGraph;


	// Todo: Fill this with necessary variables and methods.

	public Map() {

	}
	
	public Map(UndirectedGraph<String, DefaultWeightedEdge> graph) {
	  stringGraph = graph;
  }
	
	//Creates an graph for an unknown 16x16 maze
	public static UndirectedGraph<String, DefaultWeightedEdge>  UnknownMaze()
	{  
	  SimpleWeightedGraph<String, DefaultWeightedEdge> graph = new SimpleWeightedGraph(DefaultWeightedEdge.class);
	  final int dimension = 16;

	  for(int x=1; x<=dimension; x++) {
	    for(int y=1; y<=dimension; y++){
	      graph.addVertex(x+","+y);
	    }
	  }
	  
	  for(int x=1; x<=dimension; x++) {
      for(int y=1; y<=dimension; y++){
        String v1 = x+","+y;
        
        String v2 = (x+1)+","+y;
        if(graph.containsVertex(v2)) {
          if(!graph.containsEdge(v1, v2)) {
            graph.addEdge(v1,v2);
            graph.setEdgeWeight(graph.getEdge(v1, v2), 1000.0); 
          }
        }
        
        v2 = x+","+(y+1);
        if(graph.containsVertex(v2)) {
          if(!graph.containsEdge(v1, v2)) {
            graph.addEdge(v1,v2);
            graph.setEdgeWeight(graph.getEdge(v1, v2), 1000.0); 
          }
        }
        
        
      }
    }
	  
	  return graph;
	}
	/*
	 * Sets a wall between the cell and it's neighbor in the direction of dir.
	 */
	public void setWall(Point<Integer> cell, Direction dir) {
	  
	  String v1 = cell.x+","+cell.y;
	  String v2 = "";
	  
	  //East
	  if (dir.value==0) {
	    v2 = (cell.x+1)+","+cell.y;
	  }
	  
	//North
    if (dir.value==90) {
      v2 = (cell.x)+","+(cell.y+1);
    }
  //West
    if (dir.value==180) {
      v2 = (cell.x-1)+","+cell.y;
    }
  //South
    if (dir.value==270) {
      v2 = (cell.x)+","+(cell.y-1);
    }
	  

	}

	/*
	 * Sets an empty passage between the cell and it's neighbor in the input
	 * direction.
	 */
	public void setNoWall(Point<Integer> cell, Direction dir) {
	  
	  

	}

	/*
	 * Returns true if there is unknown wall data pertaining the input cell.
	 */
	public boolean needsWallData(Point<Integer> cell) {
		return false;
	}

	/*
	 * Generates random maze for the emulator.
	 */
	public void generateRandomMaze() {
	}

	public boolean wallAt(Point<Double> p) {
		return true;
	}

	public void drawMaze(Graphics g) {
		// Can be recursive.
		g.setColor(Color.LIGHT_GRAY);
		int scaleFactor = Constants.SCALE_FACTOR;
		for (int i = 1; i < 5; i++) {
			g.drawLine(0, (int) Constants.CELL_WIDTH * i * scaleFactor,
					(int) Constants.CELL_WIDTH * 5 * scaleFactor,
					(int) Constants.CELL_WIDTH * i * scaleFactor);
			g.drawLine((int) Constants.CELL_WIDTH * i * scaleFactor, 0,
					(int) Constants.CELL_WIDTH * i * scaleFactor,
					(int) Constants.CELL_WIDTH * 5 * scaleFactor);
		}
		g.setColor(Color.BLACK);
	}
}
