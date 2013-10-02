import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

/* Helper Class Coordinate */ 
class Coordinate{
	int x;
	int y;
	public Coordinate(int x, int y){
		this.x=x;
		this.y=y;
	}
}



class Edge implements Comparable<Edge> {
	int wt;
	int pt1;
	int pt2;
	Edge(int pt1, int pt2, int wt){
		this.pt1 = pt1;
		this.pt2 = pt2;
		this.wt = wt;
	}
	@Override
	public int compareTo(Edge obj) {
		return wt - obj.wt;
	}
}

public class SearchMultipleDots {
	
	class POINT implements Comparable<POINT>{
		int x;
		int y;
		int count;
		boolean goalState[][];
		int dots_eaten;
		int heuristic_value;
		int heuristic_value_2;
		List<Coordinate> outputSequence;
		// Used for BFS, DFS
		public POINT(int i, int j, int c,  int dots_eaten, boolean goalState[][], List<Coordinate> l) {
			x = i;
			y = j;
			count = c;
			this.dots_eaten = dots_eaten;
			this.goalState = goalState;
			this.outputSequence = l;
		}
		
		public POINT(int i, int j, int c,  int dots_eaten, boolean goalState[][]) {
			x = i;
			y = j;
			count = c;
			this.dots_eaten = dots_eaten;
			this.goalState = goalState;
			
		}
		
		// Used for GBFS,A*
		public POINT(int i, int j, int c, int dots_eaten, boolean goalState[][], int heuristic_value, List<Coordinate> l) {
			x = i;
			y = j;
			count = c;
			this.dots_eaten = dots_eaten;
			this.goalState = goalState;
			this.heuristic_value = heuristic_value;
			this.outputSequence = l;
		}
		
		public POINT(int i, int j, int c, int dots_eaten, boolean goalState[][], int heuristic_value, int h_2,List<Coordinate> l) {
			x = i;
			y = j;
			count = c;
			this.dots_eaten = dots_eaten;
			this.goalState = goalState;
			this.heuristic_value = heuristic_value;
			this.outputSequence = l;
			this.heuristic_value_2 = h_2;
		}
		// Used for GBFS,A*
		public POINT(int i, int j, int c, int dots_eaten, boolean goalState[][], int heuristic_value) {
					x = i;
					y = j;
					count = c;
					this.dots_eaten = dots_eaten;
					this.goalState = goalState;
					this.heuristic_value = heuristic_value;
					
				}
		
		public int findClosestWallDistance(){
			int xleft = x;
			int ytop = y;
			int xright = column - x;
			int ybottom = row - y;
			
			return Math.min(xleft, Math.min(ytop, Math.min(xright, ybottom)));
		}
		@Override
		public int compareTo(POINT obj) {
			
			if(heuristic_value != obj.heuristic_value){
				return heuristic_value - obj.heuristic_value;
			}else{
				if(dots_eaten != obj.dots_eaten){
					return -1*(dots_eaten - obj.dots_eaten);
				}
				if(heuristic_value_2 != obj.heuristic_value_2){
					return (heuristic_value_2 - obj.heuristic_value_2);
				}
				else if(findClosestWallDistance() != obj.findClosestWallDistance()){
					return 1*(findClosestWallDistance() - obj.findClosestWallDistance());
				}
				
				else return -1;
			}
		}
	}
	
	// Variable of the problem.
	char[][] maze = new char[100][100];
	int starti, startj, number_dots;
	int row, column;
	List<Coordinate> dots;
	
	/* Used to store the state information */
	class stateNode {
		boolean isExpanded;
		List<boolean [][]> visitedStates;
		// Note we could use primitive arrays with collections but basic primitive type
		// List<boolean>l will throw errors.
		stateNode(){
			isExpanded = false;
			visitedStates = new ArrayList<boolean [][]>();
		}
	}
	
	// Since arrays in java are passed by reference and we want to
	// keep separate state for each path traversed hence we will
	// have to perform deep copy operation.
	
	public void run() throws IOException
	{
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader("hw0/1.3/maze_small.txt")); 
		String line = null;
		int count = 0;
		number_dots = 0;
		int sz = 0;
		
		dots = new ArrayList<Coordinate>();
		while ((line = reader.readLine()) != null) {
			sz= line.length();
			for (int i=0; i<sz; i++) {
				maze[count][i] = line.charAt(i);
				
				if (maze[count][i] == 'P') {
					starti = count;
					startj = i;
				}
				
				if (maze[count][i] == '.') {
					
					number_dots++;
				}
			}
			count++;
			
		}
		row = count;
		column = sz;
		for(int i=0;i < row;i++){
			for(int j=0;j<column;j++){
				System.out.print(maze[i][j]);
			}
			System.out.println();
		}
		DFS();
		BFS();
		GBF();
		ASTAR(); 
	}

	@SuppressWarnings("unchecked")
	private void DFS()
	{
		stateNode shouldVisit[][] = new stateNode[row][column];
		for(int i = 0;i < row;i++){
			for(int j =0; j<column;j++){
				shouldVisit[i][j] = new stateNode();
			}
		}
		boolean[][] goalState = new boolean[row][column];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		List<Coordinate> outputSequence = null;
		
		Stack lifo = new Stack();
		lifo.add(new POINT(starti, startj, 0, 0, goalState, new ArrayList<Coordinate>()));
		shouldVisit[starti][startj] = new stateNode();
		
		
		while (!lifo.isEmpty()) {
			
			POINT top = (POINT) lifo.pop();
			int x = top.x, y = top.y, count = top.count;
			int dots_eaten = top.dots_eaten;
			boolean curr_gs[][] = top.goalState;
			List<Coordinate> l = top.outputSequence;
			
			if(maximum_tree_depth_searched < count){
				maximum_tree_depth_searched = count;
			}
			
			// Check if we need to expand this node.
			if(shouldExpand(x,y, shouldVisit, curr_gs)){
				number_nodes_expanded++;
				shouldVisit[x][y].isExpanded = true;
				shouldVisit[x][y].visitedStates.add(curr_gs);
				
				if(maze[x][y] == '.' && curr_gs[x][y] == false){
					++dots_eaten;
					l.add(new Coordinate(x, y));
					if(dots_eaten == number_dots){
						outputSequence = l;
						break;
					}
					curr_gs[x][y] = true;
					shouldVisit[x][y].visitedStates.add(deepCopy(curr_gs));
				}
				if (maze[x - 1][y] != '%' && shouldExpand(x-1, y, shouldVisit,curr_gs)){
					lifo.add(new POINT(x - 1, y, count + 1, dots_eaten, deepCopy(curr_gs), deepCopy(l)));
				}
			
				if (maze[x][y - 1] != '%' && shouldExpand(x, y-1, shouldVisit,curr_gs)) {
					lifo.add(new POINT(x, y - 1, count + 1, dots_eaten, deepCopy(curr_gs), deepCopy(l)));
				}
				
				if (maze[x][y + 1] != '%' && shouldExpand(x, y+1, shouldVisit,curr_gs)) {
					lifo.add(new POINT(x, y + 1, count + 1, dots_eaten, deepCopy(curr_gs), deepCopy(l)));
				}
				
				if (maze[x + 1][y] != '%' && shouldExpand(x+1, y, shouldVisit,curr_gs)) {
					lifo.add(new POINT(x + 1, y, count + 1, dots_eaten, deepCopy(curr_gs), deepCopy(l)));
				}
			  }	
		}
		
		System.out.println("DFS");
		System.out.println("Number of nodes expanded : " + number_nodes_expanded);
		System.out.println("Maximum Tree Depth Searched " + maximum_tree_depth_searched);
		char [][]output = new char[row][column];
		for(int i=0;i<row;i++){
			for(int j=0;j<column;j++){
				output[i][j] = maze[i][j];
			}
		}
		int j = 97;
		for(int k = 0;k<outputSequence.size();k++){
			if(k<=9)
				output[outputSequence.get(k).x][outputSequence.get(k).y]=(char)(k+48);
			else
				output[outputSequence.get(k).x][outputSequence.get(k).y]=(char)(j++);
		}
		
		for(int i=0;i<row;i++){
			for(int k=0;k<column;k++){
				System.out.print(output[i][k] + " ");
			}
			System.out.println();
		}
		System.out.println("---------------------------------------------------------");	
	}
	
	private void BFS()
	{
		stateNode shouldVisit[][] = new stateNode[row][column];
		for(int i = 0;i < row;i++){
			for(int j =0; j<column;j++){
				shouldVisit[i][j] = new stateNode();
			}
		}
		boolean[][] goalState = new boolean[row][column];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 1;
				
		Queue<POINT> fifo = new LinkedList<POINT>();
		fifo.add(new POINT(starti, startj, 0, 0, goalState, new ArrayList<Coordinate>()));
		shouldVisit[starti][startj] = new stateNode();
		List<Coordinate> outputSequence = null;
		
		while (!fifo.isEmpty()) {
			
			POINT top = (POINT) fifo.remove();
			int x = top.x, y = top.y, count = top.count;
			int dots_eaten = top.dots_eaten;
			boolean curr_gs[][] = top.goalState;
			List<Coordinate> l = top.outputSequence;
			
			if(maximum_tree_depth_searched < count){
				maximum_tree_depth_searched = count;
			}
			
			// Check if we need to expand this node.
			if(shouldExpand(x,y, shouldVisit, curr_gs)){
				number_nodes_expanded++;
				shouldVisit[x][y].isExpanded = true;
				shouldVisit[x][y].visitedStates.add(curr_gs);
				
				if(maze[x][y] == '.' && curr_gs[x][y] == false){
					++dots_eaten;
					l.add(new Coordinate(x, y));
					if(dots_eaten == number_dots){
						outputSequence = l;
						break;
					}
					curr_gs[x][y] = true;
					
					shouldVisit[x][y].visitedStates.add(deepCopy(curr_gs));
				}
				if (maze[x - 1][y] != '%' && shouldExpand(x-1, y, shouldVisit,curr_gs)){
					fifo.add(new POINT(x - 1, y, count + 1, dots_eaten, deepCopy(curr_gs), deepCopy(l)));
				}
			
				if (maze[x][y - 1] != '%' && shouldExpand(x, y-1, shouldVisit,curr_gs)) {
					fifo.add(new POINT(x, y - 1, count + 1, dots_eaten, deepCopy(curr_gs), deepCopy(l)));
				}
				
				if (maze[x][y + 1] != '%' && shouldExpand(x, y+1, shouldVisit,curr_gs)) {
					fifo.add(new POINT(x, y + 1, count + 1, dots_eaten, deepCopy(curr_gs), deepCopy(l)));
				}
				
				if (maze[x + 1][y] != '%' && shouldExpand(x+1, y, shouldVisit,curr_gs)) {
					fifo.add(new POINT(x + 1, y, count + 1, dots_eaten, deepCopy(curr_gs), deepCopy(l)));
				}
			  }	
		}
		
		System.out.println("BFS");
		System.out.println("Number of nodes expanded : " + number_nodes_expanded);
		System.out.println("Maximum Tree Depth Searched " + maximum_tree_depth_searched);
		char [][]output = new char[row][column];
		for(int i=0;i<row;i++){
			for(int j=0;j<column;j++){
				output[i][j] = maze[i][j];
			}
		}
		int j = 97;
		for(int k = 0;k<outputSequence.size();k++){
			if(k<=9)
				output[outputSequence.get(k).x][outputSequence.get(k).y]=(char)(k+48);
			else
				output[outputSequence.get(k).x][outputSequence.get(k).y]=(char)(j++);
		}
		
		for(int i=0;i<row;i++){
			for(int k=0;k<column;k++){
				System.out.print(output[i][k] + " ");
			}
			System.out.println();
		}
		System.out.println("---------------------------------------------------------");
	}
	
	
	
	private void GBF()
	{
		stateNode shouldVisit[][] = new stateNode[row][column];
		for(int i = 0;i < row;i++){
			for(int j =0; j<column;j++){
				shouldVisit[i][j] = new stateNode();
			}
		}
		boolean[][] goalState = new boolean[row][column];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		Queue<POINT> pq = new PriorityQueue<POINT>();
		pq.add(new POINT(starti, startj, 0, 0, goalState, new ArrayList<Coordinate>()));
		shouldVisit[starti][startj] = new stateNode();
		List<Coordinate> outputSequence = null;

		while (!pq.isEmpty()) {
			
			POINT top = (POINT) pq.remove();
			int x = top.x, y = top.y, count = top.count;
			int dots_eaten = top.dots_eaten;
			boolean curr_gs[][] = top.goalState;
			List<Coordinate> l = top.outputSequence;
			
			if(maximum_tree_depth_searched < count){
				maximum_tree_depth_searched = count;
			}
			
			// Check if we need to expand this node.
			if(shouldExpand(x,y, shouldVisit, curr_gs)){
				//System.out.println("Expanding - " + x + " " + y);
				
				number_nodes_expanded++;
				shouldVisit[x][y].isExpanded = true;
				shouldVisit[x][y].visitedStates.add(curr_gs);
				
				if(maze[x][y] == '.' && curr_gs[x][y] == false){
					++dots_eaten;
					l.add(new Coordinate(x, y));
					if(dots_eaten == number_dots){
						outputSequence = l;
						break;
					}
					curr_gs[x][y] = true;
					shouldVisit[x][y].visitedStates.add(deepCopy(curr_gs));
				}
				//System.out.println("Dots Eaten - " + dots_eaten);
				
				
				if (maze[x][y - 1] != '%' ) {
					int heuristic_val = computeHeuristicValue(x, y-1, maze, curr_gs, new boolean[row][column],2);
					pq.add(new POINT(x, y - 1, count + 1, dots_eaten, deepCopy(curr_gs), heuristic_val,deepCopy(l)));
				}
				if (maze[x][y + 1] != '%') {
					int heuristic_val = computeHeuristicValue(x, y+1, maze, curr_gs, new boolean[row][column],2);
					pq.add(new POINT(x, y + 1, count + 1, dots_eaten, deepCopy(curr_gs), heuristic_val, deepCopy(l)));
				}
				if (maze[x + 1][y] != '%' ) {
					int heuristic_val = computeHeuristicValue(x+1, y, maze, curr_gs, new boolean[row][column],2);
					pq.add(new POINT(x + 1, y, count + 1, dots_eaten, deepCopy(curr_gs), heuristic_val, deepCopy(l)));
				}
				
				if (maze[x - 1][y] != '%' ){
					int heuristic_val = computeHeuristicValue(x-1, y, maze, curr_gs, new boolean[row][column],2);
					pq.add(new POINT(x - 1, y, count + 1, dots_eaten, deepCopy(curr_gs), heuristic_val, deepCopy(l)));
				}
			  }	
			
		}
		System.out.println("GBFS");
		System.out.println("Number of nodes expanded : " + number_nodes_expanded);
		System.out.println("Maximum Tree Depth Searched " + maximum_tree_depth_searched);
		char [][]output = new char[row][column];
		for(int i=0;i<row;i++){
			for(int j=0;j<column;j++){
				output[i][j] = maze[i][j];
			}
		}
		int j = 97;
		for(int k = 0;k<outputSequence.size();k++){
			if(k<=9)
				output[outputSequence.get(k).x][outputSequence.get(k).y]=(char)(k+48);
			else
				output[outputSequence.get(k).x][outputSequence.get(k).y]=(char)(j++);
		}
		
		for(int i=0;i<row;i++){
			for(int k=0;k<column;k++){
				System.out.print(output[i][k] + " ");
			}
			System.out.println();
		}
		System.out.println("---------------------------------------------------------");
	}

	private void ASTAR()
	{
		stateNode shouldVisit[][] = new stateNode[row][column];
		for(int i = 0;i < row;i++){
			for(int j =0; j<column;j++){
				shouldVisit[i][j] = new stateNode();
			}
		}
		boolean[][] goalState = new boolean[row][column];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		List<Coordinate> outputSequence = null;
		Queue<POINT> pq = new PriorityQueue<POINT>();
		pq.add(new POINT(starti, startj, 0, 0, goalState, new ArrayList<Coordinate>()));
		shouldVisit[starti][startj] = new stateNode();
		
		
		while (!pq.isEmpty()) {
			
			POINT top = (POINT) pq.remove();
			int x = top.x, y = top.y, count = top.count;
			int dots_eaten = top.dots_eaten;
			boolean curr_gs[][] = top.goalState;
			List<Coordinate> l = top.outputSequence;
			
			if(maximum_tree_depth_searched < count){
				maximum_tree_depth_searched = count;
			}
			
			// Check if we need to expand this node.
			if(shouldExpand(x,y, shouldVisit, curr_gs)){
				//System.out.println("Expanding - " + x + " " + y);
				
				number_nodes_expanded++;
				shouldVisit[x][y].isExpanded = true;
				shouldVisit[x][y].visitedStates.add(curr_gs);
				
				if(maze[x][y] == '.' && curr_gs[x][y] == false){
					++dots_eaten;
					l.add(new Coordinate(x, y));
					if(dots_eaten == number_dots){
						outputSequence = l;
						break;
					}
					curr_gs[x][y] = true;
					shouldVisit[x][y].visitedStates.add(deepCopy(curr_gs));
				}
				//System.out.println("Dots Eaten - " + dots_eaten);
				
				
				
				if (maze[x][y - 1] != '%' && shouldExpand(x, y-1, shouldVisit,curr_gs)) {
					int heuristic_val = count + computeHeuristicValue(x, y-1, maze, curr_gs, new boolean[row][column],1);
					int dotPresent = closestDotDistance(x, y, maze, goalState) ;
					if(maze[x][y-1]=='.' && curr_gs[x][y-1]==false) dotPresent = 1;
					pq.add(new POINT(x, y - 1, count + 1, dots_eaten, deepCopy(curr_gs), heuristic_val, dotPresent, deepCopy(l)));
				}
				if (maze[x][y + 1] != '%' && shouldExpand(x, y+1, shouldVisit,curr_gs)) {
					int heuristic_val = count + computeHeuristicValue(x, y+1, maze, curr_gs, new boolean[row][column],1);
					int dotPresent = closestDotDistance(x, y, maze, goalState) ;
					if(maze[x][y+1]=='.' && curr_gs[x][y+1]==false) dotPresent = 1;
					pq.add(new POINT(x, y + 1, count + 1, dots_eaten, deepCopy(curr_gs), heuristic_val, dotPresent, deepCopy(l)));
				}
				if (maze[x + 1][y] != '%' && shouldExpand(x+1, y, shouldVisit,curr_gs)) {
					int heuristic_val = count + computeHeuristicValue(x+1, y, maze, curr_gs, new boolean[row][column],1);
					int dotPresent = closestDotDistance(x, y, maze, goalState) ;
					//if(maze[x+1][y]=='.' && curr_gs[x+1][y]==false) dotPresent = 1;
					pq.add(new POINT(x + 1, y, count + 1, dots_eaten, deepCopy(curr_gs), heuristic_val, dotPresent, deepCopy(l)));
				}	
				
				
				if (maze[x - 1][y] != '%' && shouldExpand(x-1, y, shouldVisit,curr_gs)){
					int heuristic_val = count + computeHeuristicValue(x-1, y, maze, curr_gs, new boolean[row][column],1);
					int dotPresent = closestDotDistance(x, y, maze, goalState) ;
					if(maze[x-1][y]=='.' && curr_gs[x-1][y]==false) dotPresent = 1;
					pq.add(new POINT(x - 1, y, count + 1, dots_eaten, deepCopy(curr_gs), heuristic_val, dotPresent, deepCopy(l)));
				}
				
			  }	
		}
		System.out.println("A*");
		System.out.println("Number of nodes expanded : " + number_nodes_expanded);
		System.out.println("Maximum Tree Depth Searched " + maximum_tree_depth_searched);
		char [][]output = new char[row][column];
		for(int i=0;i<row;i++){
			for(int j=0;j<column;j++){
				output[i][j] = maze[i][j];
			}
		}
		int j = 97;
		for(int k = 0;k<outputSequence.size();k++){
			if(k<=9)
				output[outputSequence.get(k).x][outputSequence.get(k).y]=(char)(k+48);
			else
				output[outputSequence.get(k).x][outputSequence.get(k).y]=(char)(j++);
		}
		
		for(int i=0;i<row;i++){
			for(int k=0;k<column;k++){
				System.out.print(output[i][k] + " ");
			}
			System.out.println();
		}
		System.out.println("---------------------------------------------------------");
	}
	
	public int closestDotDistance(int x, int y, char [][]maze, boolean [][]goalState){
		int minDist = 9999;
		int xclosest = -1;
		int yclosest = -1;
		int xfarthest = xclosest;
		int yfarthest = yclosest;
		for(int i = 0; i< row; i++){
			for(int j = 0; j < column; j++){
				if(maze[i][j] == '.' && goalState[i][j] == false) {
					if((Math.abs(x-i) + Math.abs(y-j) < minDist)){
						minDist = Math.min(minDist, (Math.abs(x-i) + Math.abs(y-j)));
						xclosest = i;
						yclosest = j;
					}	
				}
			}
		}
		if(minDist == 9999) return 0;
		
		
		return minDist;
		//int degree = 0;
		
	}
	public static void main(String[] args) throws IOException
	{
		SearchMultipleDots main = new SearchMultipleDots();
		main.run();
		System.exit(0);
	}
	
	/*
	 * Below is a list of the helper functions
	 * that are used to perform specific tasks.
	 */
	
	/*
	 * This function is used for deep copying of the boolean array. 
	 */
	public boolean[][] deepCopy(boolean[][] original) {
	    if (original == null) {
	        return null;
	    }

	    boolean[][] result = new boolean[original.length][];
	    for (int i = 0; i < original.length; i++) {
	        result[i] = Arrays.copyOf(original[i], original[i].length);
	    }
	    
	    return result;
	}
	
	/*
	 * This function is used for deep copying of the list of coordinates.
	 */
	public List<Coordinate> deepCopy(List<Coordinate> original) {
	    if (original == null) {
	        return null;
	    }
	    List<Coordinate> result = new ArrayList<Coordinate>();
	    
	    for (int i = 0; i < original.size(); i++) {
	        int x = original.get(i).x;
	        int y = original.get(i).y;
	    	result.add(new Coordinate(x, y));
	    }
	    
	    return result;
	}
	
	/*
	 * Computes the minimal spanning tree for a list of dots.
	 */
	private int computeMinimalSpanningTreeWeight(List<Coordinate> dots){
		// build the graph using dots;
		//System.out.println("Computing minimal spanning tree");
		int sz = dots.size();
		int graph[][] = new int[sz][sz];
		int mst[][] = new int[sz][sz];
		List<Edge> queue = new ArrayList<Edge>();
		for(int i = 0;i<sz;i++){
			for(int j = i+1;j<sz;j++){
				// compute distance between node i and node j
				Coordinate p = dots.get(i);
				Coordinate q = dots.get(j);
				int wt = Math.abs(p.x - q.x) + Math.abs(p.y - q.y);
				graph[i][j] = wt;
				//System.out.print(graph[i][j] + " ");
				queue.add(new Edge(i,j,wt));
			}
			//System.out.println();
		}
		Collections.sort(queue);
		boolean chosenNodes[] = new boolean[sz];
		chosenNodes[0] = true;
		List<Integer> edges = new ArrayList<Integer>();
		int result = 0;
		while(true){
			for(int i = 0;i<queue.size();i++){
			Edge e = queue.get(i);
			// Check if we can select this edge
			
				if((chosenNodes[e.pt1] == true && chosenNodes[e.pt2] ==false) || (chosenNodes[e.pt1] == false && chosenNodes[e.pt2] == true)){
					result = result + e.wt;
					edges.add(e.wt);
					chosenNodes[e.pt1]=true;
					chosenNodes[e.pt2]=true;
					mst[e.pt1][e.pt2]=e.wt;
					mst[e.pt2][e.pt1]=e.wt;
					break;
				}
			}	
			// Check for the terminating condition.
			boolean allVerticesSelected = true;
			for(int k = 0;k<sz;k++){
				allVerticesSelected = allVerticesSelected & chosenNodes[k];
			}
			if(allVerticesSelected) break;
			
		}
		// Now we need to traverse the mst to find the total path cost.
		//result = computeCostofMST(0, mst, new boolean[sz][sz]);
		return result;
	}
	
	int computeCostofMST(int row,int mst[][], boolean visited[][]){
		int result = 0;
		for(int k =0;k<mst.length;k++){
			if(mst[row][k]!=0 && visited[row][k] == false){
				visited[row][k] = true;
				visited[k][row] = true;
				result = result + mst[row][k] + computeCostofMST(k, mst, visited);
			}
		}
		
		return result;
	}
	
	/* 
	 * Computes the heuristic-value of a point 
	 * type - 1 :  admissible heuristics
	 * type - 2  : non-admissible heuristics
	 */
	public int computeHeuristicValue(int x, int y, char [][]maze, boolean [][]goalState, boolean visited[][], int type){
		
		if(type==1){
		int xmin = x, ymin = y;
		int xmax = x, ymax = y;
		for(int i = 0; i< row; i++){
			for(int j = 0; j < column; j++){
				if(maze[i][j] == '.' && goalState[i][j] == false) {
					xmin = Math.min(i, xmin);
					xmax = Math.max(i, xmax);
					ymin = Math.min(j, ymin);
					ymax = Math.max(j, ymax);
				}
			}
		}
		//int h_1 = (xmax-xmin) + (ymax-ymin);
		
		List<Integer> l = new ArrayList<Integer>();
		l.add(Math.abs(x-xmax));
		l.add(Math.abs(x-xmin));
		l.add(Math.abs(y-ymin));
		l.add(Math.abs(y-ymax));
		Collections.sort(l);
		int h_1 = 2*(l.get(0) + l.get(1) + l.get(2)) + l.get(3);
		//int h_1 = l.get(3);
		dots.add(new Coordinate(x, y));
		for(int i = 0; i< row; i++){
			for(int j = 0; j < column; j++){
				if(maze[i][j] == '.' && goalState[i][j] == false) {
					dots.add(new Coordinate(i, j));
				}
			}
		}
		int h_2 = computeMinimalSpanningTreeWeight(dots);
		dots.clear();
		return Math.max(h_1, h_2); 
		}else{
		int min = +99999;
		int xNext=-1, yNext=-1;
		visited[x][y] = true;
		for(int i = 0; i< row; i++){
			for(int j = 0; j < column; j++){
				if(maze[i][j] == '.' && goalState[i][j] == false && visited[i][j] == false) {
					if((Math.abs(x-i) + Math.abs(y-j)) < min){
						min = Math.abs(x-i) + Math.abs(y-j);
						xNext=i;
						yNext=j;
					}
				}
			}
		}
		if(min == 99999) return 0;
		else return min + computeHeuristicValue(xNext, yNext, maze, goalState, visited,2);}
	}
	
	/*
	 * 
	 */
	private boolean shouldExpand(int x, int y, stateNode shouldVisit[][], boolean [][]currState){
		
		if(shouldVisit[x][y].isExpanded == false){
			return true;
		}else{
			// Check for what output states have we expanded this node  
			for(int i = 0; i < shouldVisit[x][y].visitedStates.size(); i++){
				boolean visitedGoalState[][] = shouldVisit[x][y].visitedStates.get(i);
				boolean tmp = true;
				for(int l = 0;l< row;l++){
					for(int m = 0;m < column;m++){
						if(currState[l][m] == visitedGoalState[l][m]) tmp = tmp & true;
						else tmp = tmp & false;
					}
				}
				if(tmp == true) return false;
			}
			return true;
		}
	}
}