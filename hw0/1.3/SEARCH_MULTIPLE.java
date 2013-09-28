import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class SEARCH_MULTIPLE {
	
	char[][] maze = new char[100][100];
	int starti, startj, number_dots;
	int row, column;
	
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
	
	private class POINT implements Comparable<POINT>{
		int x;
		int y;
		int count;
		boolean goalState[][];
		int dots_eaten;
		int heuristic_value;
		
		// Used for BFS, DFS
		public POINT(int i, int j, int c,  int dots_eaten, boolean goalState[][]) {
			x = i;
			y = j;
			count = c;
			this.dots_eaten = dots_eaten;
			this.goalState = goalState;
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
		@Override
		public int compareTo(POINT obj) {
			return heuristic_value - obj.heuristic_value;
		}
		
	}
	
	// Since arrays in java are passed by reference and we want to
	// keep separate state for each path traversed hence we will
	// have to perform deep copy operation.
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
	
	/* greedily calculate the path cost */
	public int computeHeuristicValue(int x, int y, char [][]maze, boolean [][]goalState, boolean visited[][]){
		/*int min = +99999;
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
		else return min + computeHeuristicValue(xNext, yNext, maze, goalState, visited);*/ 		
		int result = 0; 
		for(int i = 0; i< row; i++){
			for(int j = 0; j < column; j++){
				if(maze[i][j] == '.' && goalState[i][j] == false) {
					result += 1;
				}
			}
		}
		return result;
		
	}
	public void run() throws IOException
	{
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader("maze_tiny.txt")); 
		String line = null;
		int count = 0;
		number_dots = 0;
		int sz = 0;
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
		
		@SuppressWarnings("rawtypes")
		Stack lifo = new Stack();
		lifo.add(new POINT(starti, startj, 0, 0, goalState));
		shouldVisit[starti][startj] = new stateNode();
		
		
		while (!lifo.isEmpty()) {
			
			POINT top = (POINT) lifo.pop();
			int x = top.x, y = top.y, count = top.count;
			int dots_eaten = top.dots_eaten;
			boolean curr_gs[][] = top.goalState;
			
			
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
					if(dots_eaten == number_dots) break;
					curr_gs[x][y] = true;
					shouldVisit[x][y].visitedStates.add(deepCopy(curr_gs));
				}
				
				
					
					if (maze[x - 1][y] != '%' && shouldExpand(x-1, y, shouldVisit,curr_gs)){
						lifo.add(new POINT(x - 1, y, count + 1, dots_eaten, deepCopy(curr_gs)));
					}
				
					if (maze[x][y - 1] != '%' && shouldExpand(x, y-1, shouldVisit,curr_gs)) {
						lifo.add(new POINT(x, y - 1, count + 1, dots_eaten, deepCopy(curr_gs)));
					}
					
					if (maze[x][y + 1] != '%' && shouldExpand(x, y+1, shouldVisit,curr_gs)) {
						lifo.add(new POINT(x, y + 1, count + 1, dots_eaten, deepCopy(curr_gs)));
					}
					
					if (maze[x + 1][y] != '%' && shouldExpand(x+1, y, shouldVisit,curr_gs)) {
						lifo.add(new POINT(x + 1, y, count + 1, dots_eaten, deepCopy(curr_gs)));
					}
			  }	
		}
		
		System.out.println("DFS");
		System.out.println("Number of nodes expanded : " + number_nodes_expanded);
		System.out.println("Maximum Tree Depth Searched " + maximum_tree_depth_searched);
		System.out.println("---------------------------------------------------------");	

		
	}
	
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
		int maximum_tree_depth_searched = 0;
				
		Queue<POINT> fifo = new LinkedList<POINT>();
		fifo.add(new POINT(starti, startj, 0, 0, goalState));
		shouldVisit[starti][startj] = new stateNode();
		
		
		while (!fifo.isEmpty()) {
			
			POINT top = (POINT) fifo.remove();
			int x = top.x, y = top.y, count = top.count;
			int dots_eaten = top.dots_eaten;
			boolean curr_gs[][] = top.goalState;
			
			
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
					if(dots_eaten == number_dots) break;
					curr_gs[x][y] = true;
					shouldVisit[x][y].visitedStates.add(deepCopy(curr_gs));
				}
				if (maze[x - 1][y] != '%' && shouldExpand(x-1, y, shouldVisit,curr_gs)){
					fifo.add(new POINT(x - 1, y, count + 1, dots_eaten, deepCopy(curr_gs)));
				}
			
				if (maze[x][y - 1] != '%' && shouldExpand(x, y-1, shouldVisit,curr_gs)) {
					fifo.add(new POINT(x, y - 1, count + 1, dots_eaten, deepCopy(curr_gs)));
				}
				
				if (maze[x][y + 1] != '%' && shouldExpand(x, y+1, shouldVisit,curr_gs)) {
					fifo.add(new POINT(x, y + 1, count + 1, dots_eaten, deepCopy(curr_gs)));
				}
				
				if (maze[x + 1][y] != '%' && shouldExpand(x+1, y, shouldVisit,curr_gs)) {
					fifo.add(new POINT(x + 1, y, count + 1, dots_eaten, deepCopy(curr_gs)));
				}
			  }	
		}
		
		System.out.println("BFS");
		System.out.println("Number of nodes expanded : " + number_nodes_expanded);
		System.out.println("Maximum Tree Depth Searched " + maximum_tree_depth_searched);
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
		pq.add(new POINT(starti, startj, 0, 0, goalState));
		shouldVisit[starti][startj] = new stateNode();
		
		
		while (!pq.isEmpty()) {
			
			POINT top = (POINT) pq.remove();
			int x = top.x, y = top.y, count = top.count;
			int dots_eaten = top.dots_eaten;
			boolean curr_gs[][] = top.goalState;
			
			
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
					if(dots_eaten == number_dots) break;
					curr_gs[x][y] = true;
					shouldVisit[x][y].visitedStates.add(deepCopy(curr_gs));
				}
				//System.out.println("Dots Eaten - " + dots_eaten);
				
					
				if (maze[x - 1][y] != '%' ){
					int heuristic_val = computeHeuristicValue(x-1, y, maze, curr_gs, new boolean[row][column]);
					pq.add(new POINT(x - 1, y, count + 1, dots_eaten, deepCopy(curr_gs), heuristic_val));
				}
			
				if (maze[x][y - 1] != '%' ) {
					int heuristic_val = computeHeuristicValue(x, y-1, maze, curr_gs, new boolean[row][column]);
					pq.add(new POINT(x, y - 1, count + 1, dots_eaten, deepCopy(curr_gs), heuristic_val));
				}
				
				if (maze[x][y + 1] != '%') {
					int heuristic_val = computeHeuristicValue(x, y+1, maze, curr_gs, new boolean[row][column]);
					pq.add(new POINT(x, y + 1, count + 1, dots_eaten, deepCopy(curr_gs), heuristic_val));
				}
				
				if (maze[x + 1][y] != '%' ) {
					int heuristic_val = computeHeuristicValue(x+1, y, maze, curr_gs, new boolean[row][column]);
					pq.add(new POINT(x + 1, y, count + 1, dots_eaten, deepCopy(curr_gs), heuristic_val));
				}
			  }	
			
		}
		System.out.println("GBFS");
		System.out.println("Number of nodes expanded : " + number_nodes_expanded);
		System.out.println("Maximum Tree Depth Searched " + maximum_tree_depth_searched);
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
		Queue<POINT> pq = new PriorityQueue<POINT>();
		pq.add(new POINT(starti, startj, 0, 0, goalState));
		shouldVisit[starti][startj] = new stateNode();
		
		
		while (!pq.isEmpty()) {
			
			POINT top = (POINT) pq.remove();
			int x = top.x, y = top.y, count = top.count;
			int dots_eaten = top.dots_eaten;
			boolean curr_gs[][] = top.goalState;
			
			
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
					if(dots_eaten == number_dots) break;
					curr_gs[x][y] = true;
					shouldVisit[x][y].visitedStates.add(deepCopy(curr_gs));
				}
				//System.out.println("Dots Eaten - " + dots_eaten);
				
					
					if (maze[x - 1][y] != '%' ){
						int heuristic_val = count + computeHeuristicValue(x-1, y, maze, curr_gs, new boolean[row][column]);
						pq.add(new POINT(x - 1, y, count + 1, dots_eaten, deepCopy(curr_gs), heuristic_val));
					}
				
					if (maze[x][y - 1] != '%' ) {
						int heuristic_val = count + computeHeuristicValue(x, y-1, maze, curr_gs, new boolean[row][column]);
						pq.add(new POINT(x, y - 1, count + 1, dots_eaten, deepCopy(curr_gs), heuristic_val));
					}
					
					if (maze[x][y + 1] != '%') {
						int heuristic_val = count + computeHeuristicValue(x, y+1, maze, curr_gs, new boolean[row][column]);
						pq.add(new POINT(x, y + 1, count + 1, dots_eaten, deepCopy(curr_gs), heuristic_val));
					}
					
					if (maze[x + 1][y] != '%' ) {
						int heuristic_val = count + computeHeuristicValue(x+1, y, maze, curr_gs, new boolean[row][column]);
						pq.add(new POINT(x + 1, y, count + 1, dots_eaten, deepCopy(curr_gs), heuristic_val));
					}
			  }	
			
		}
		System.out.println("A*");
		System.out.println("Number of nodes expanded : " + number_nodes_expanded);
		System.out.println("Maximum Tree Depth Searched " + maximum_tree_depth_searched);
		System.out.println("---------------------------------------------------------");
		
		
	}
	
	public static void main(String[] args) throws IOException
	{
		SEARCH_MULTIPLE main = new SEARCH_MULTIPLE();
		main.run();
		System.exit(0);
	}
}