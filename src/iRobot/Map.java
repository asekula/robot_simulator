package iRobot;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;

import org.jgraph.JGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.ext.JGraphModelAdapter;
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
	  
	  for(int i=1; i<=16; i++) {
	    for(int j=1; j<=16; j++){
	      graph.addVertex(i+","+j);
	    }
	  }
	  
	  for(int i=1; i<=16; i++) {
      for(int j=1; j<=16; j++){
        String v1 = i+","+j;
        
        String v2 = (i+1)+","+j;
        if(graph.containsVertex(v2)) {
          if(!graph.containsEdge(v1, v2)) {
            graph.addEdge(v1,v2);
            graph.setEdgeWeight(graph.getEdge(v1, v2), 1000.0); 
          }
        }
        
        v2 = i+","+(j+1);
        if(graph.containsVertex(v2)) {
          if(!graph.containsEdge(v1, v2)) {
            graph.addEdge(v1,v2);
            graph.setEdgeWeight(graph.getEdge(v1, v2), 1000.0); 
          }
        }
        
        
        
      }
    }
	  JFrame frame = new JFrame();
	  frame.setSize(400, 400);
	 
    JGraph jgraph = new JGraph( new JGraphModelAdapter( graph ) );	  
    frame.getContentPane().add(jgraph);
    frame.setVisible(true);
	  System.out.println("Executed");
	  while (true) {
	    try {
        Thread.sleep(20000);
        return graph;

      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
	    }
	  
	}
	/*
	 * Sets a wall between the cell and it's neighbor in the direction of dir.
	 */
	public void setWall(Point<Integer> cell, Direction dir) {

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
