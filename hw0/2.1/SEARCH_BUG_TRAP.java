import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class SEARCH_BUG_TRAP {
	
	char[][] maze = new char[100][100];
	char[][] mazeModified;
	int starti, startj, endi, endj, maxx, maxy;
	int gloabal_nodes_expanded, global_path_cost, global_tree_depth, global_frontier_size;
	int[] MAZE_STEPS = {-1, 0, 1};
	String FOLDER = "hw0/2.1/input_files/";
	String FILE = "maze_open";
	String RESULT = "hw0/2.1/results/";
	boolean print;
	
	private class POINT implements Comparable<POINT> {
		int x;
		int y;
		int count;
		int heuristic;
		
		public POINT(int i, int j, int c) {
			x = i;
			y = j;
			count = c;
		}
		
		public POINT(int i, int j, int c, int h) {
			x = i;
			y = j;
			count = c;
			heuristic = h;
		}
		
		@Override
		public int compareTo(POINT obj) {
			return heuristic - obj.heuristic;
		}
	}
	
	public void run() throws IOException, InterruptedException
	{
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(FOLDER + FILE + ".txt"));
		String line = null;
		int count = 0;
		print = true;
		
		while ((line = reader.readLine()) != null && !line.equals("")) {
			maxy = line.length();
			for (int i=0; i<line.length(); i++) {
				maze[count][i] = line.charAt(i);
				
				if (maze[count][i] == 'P') {
					starti = count;
					startj = i;
				}
				
				if (maze[count][i] == '.') {
					endi = count;
					endj = i;
				}
			}
			count++;
		}
		maxx = count;

		ASTAR();
		OURHEURISTIC();
		
		gloabal_nodes_expanded = 0;
		global_path_cost = 0;
		global_tree_depth = 0;
		global_frontier_size = 0;
		int startiLocal = starti;
		int startjLocal = startj;
		print = false;
		
		char[][] maze_copy = deepCopyAnotherMaze(maze);
		for (int i=0; i<1000; i++) {
			maze =  deepCopyAnotherMaze(maze_copy);
			starti = startiLocal;
			startj = startjLocal;
			getAnotherStartPosition();
			ASTAR();
		}
		
		System.out.println("Average Path cost : " + global_path_cost/1000);
		System.out.println("Average Number of nodes expanded : " + gloabal_nodes_expanded/1000);
		System.out.println("Average Maximum tree depth searched " + global_tree_depth/1000);
		System.out.println("Average Maximum size of the frontier " + global_frontier_size/1000);
	}
	
	private void getAnotherStartPosition()
	{
		int moves = (int) (Math.random() * 50);
		int direction = (int) (Math.random() * 4);
		int x, y;
		
		if (direction == 0) {
			x = -1; y = 0;
		}
		else if (direction == 1) {
			x = 1; y = 0;
		}
		else if (direction == 2) {
			x = 0; y = -1;
		}
		else {
			x = 0; y = 1;
		}

		maze[starti][startj] = ' ';
		for (int i=0; i<moves; i++) {
			if (maze[starti + x][startj + y] == '%') {
				break;
			}
			starti += x;
			startj += y;
		}
		
		maze[starti][startj] = 'P';
	}
	
	private void ASTAR() throws InterruptedException, FileNotFoundException, UnsupportedEncodingException
	{
		boolean[][] isVisited = new boolean[100][100];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		int path_cost = 0;
		int maximum_size_frontier = 0;
		
		deepCopyMaze();

		Queue<POINT> queue = new PriorityQueue<>();
		queue.add(new POINT(starti, startj, 0, Math.abs(endi - starti) + Math.abs(endj - startj)));
		isVisited[starti][startj] = true;
		
		while (!queue.isEmpty()) {
			if (queue.size() > maximum_size_frontier) { 
				maximum_size_frontier = queue.size();
			}
			
			POINT top = queue.remove();
			
			int x = top.x, y = top.y, count = top.count;
			
			if (maximum_tree_depth_searched < count) {
				maximum_tree_depth_searched = count;
			}
			if (maze[x][y] == '.') {
				path_cost = count;
				break;
			}
			number_nodes_expanded++;
			
			mazeModified[x][y] = '.';
//			clrscr();
//			printMaze(mazeModified);
//			Thread.sleep(150);
			
			for (int i : MAZE_STEPS) {
				for (int j : MAZE_STEPS) {
					if (Math.abs(i) != Math.abs(j) &&
						x + i >= 0 && x + i < maxx &&
						y + j >= 0 && y + j < maxy)
					{
						if (isValid(x + i, y + j, isVisited)) {
							queue.add(new POINT(x + i, y + j, count + 1, count + 1
									+ Math.abs(endi - x - i)
									+ Math.abs(endj - y - j)));
						}
					}
				}
			}
		}
		
		writeToFile("astar");
		
		global_path_cost += path_cost;
		gloabal_nodes_expanded += number_nodes_expanded;
		global_tree_depth += maximum_tree_depth_searched;
		global_frontier_size += maximum_size_frontier;
		
		if (print) {
			System.out.println("A*");
			System.out.println("Path cost : " + path_cost);
			System.out.println("Number of nodes expanded : "
					+ number_nodes_expanded);
			System.out.println("Maximum tree depth searched "
					+ maximum_tree_depth_searched);
			System.out.println("Maximum size of the frontier "
					+ maximum_size_frontier);
			System.out.println("---------------------------------------------------------");	
		}
	}
	
	private boolean isValid(int x, int y, boolean[][] isVisited)
	{
		if (maze[x][y] == '%' || isVisited[x][y])
		{
			return false;
		}
		else {
			isVisited[x][y] = true;
			return true;
		}
	}
	
	private void deepCopyMaze()
	{
		mazeModified = new char[100][100];
		for (int i=0; i<maze.length; i++) {
			for (int j=0; j<maze[0].length; j++) {
				mazeModified[i][j] = maze[i][j];
			}
		}
	}
	
	private char[][] deepCopyAnotherMaze(char[][] mazeLocal)
	{
		char[][] m = new char[100][100];
		for (int i=0; i<mazeLocal.length; i++) {
			for (int j=0; j<mazeLocal[0].length; j++) {
				m[i][j] = mazeLocal[i][j];
			}
		}
		return m;
	}
	
	private void printMaze(char[][] m)
	{
		for (int i=0; i<maxx; i++) {
			for (int j=0; j<maxy; j++) {
				System.out.print(m[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}
	
	private void clrscr()
	{
		char c = '\n';
		int length = 20;
		char[] chars = new char[length];
		Arrays.fill(chars, c);
		System.out.print(String.valueOf(chars));
	}
	
	private void writeToFile(String s) throws FileNotFoundException, UnsupportedEncodingException
	{
		PrintWriter writer = new PrintWriter(RESULT + FILE + "_" + s + ".txt", "UTF-8");
		for (int i=0; i<maxx; i++) {
			for (int j=0;j<maxy; j++) {
				writer.print(mazeModified[i][j]);
			}
			writer.println();
		}
		writer.close();
	}

	private boolean hasNeighborAsWall(int x, int y)
	{
		for (int i : MAZE_STEPS) {
			for (int j : MAZE_STEPS) {
				if (x + i >= 0 && x + i < maxx &&
					y + j >= 0 && y + j < maxy)
				{
					if (maze[x + i][y + j] == '%') {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private void OURHEURISTIC() throws InterruptedException, FileNotFoundException, UnsupportedEncodingException
	{
		boolean[][] isVisited = new boolean[100][100];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		int path_cost = 0;
		int maximum_size_frontier = 0;
		
		deepCopyMaze();

		Queue<POINT> queue = new PriorityQueue<>();
		queue.add(new POINT(starti, startj, 0, Math.abs(endi - starti) + Math.abs(endj - startj)));
		isVisited[starti][startj] = true;
		
		while (!queue.isEmpty()) {
			if (queue.size() > maximum_size_frontier) { 
				maximum_size_frontier = queue.size();
			}
			
			POINT top = queue.remove();
			
			int x = top.x, y = top.y, count = top.count;
			
			if (maximum_tree_depth_searched < count) {
				maximum_tree_depth_searched = count;
			}
			if (maze[x][y] == '.') {
				path_cost = count;
				break;
			}
			number_nodes_expanded++;
			
			mazeModified[x][y] = '.';
//			clrscr();
//			printMaze(mazeModified);
//			Thread.sleep(150);
			
			for (int i : MAZE_STEPS) {
				for (int j : MAZE_STEPS) {
					if (Math.abs(i) != Math.abs(j) &&
						x + i >= 0 && x + i < maxx &&
						y + j >= 0 && y + j < maxy)
					{
						int h = Math.max(Math.abs(endi - x - i), Math.abs(endj - y - j));
						if (!hasNeighborAsWall(x + i, y + j)) {
							h *= 10;
						}
						if (isValid(x + i, y + j, isVisited)) {
							queue.add(new POINT(x + i, y + j, count + 1, h));
						}
					}
				}
			}
		}
		
		writeToFile("our_heuristic");
		
		System.out.println("Our heuristic : ");
		System.out.println("Path cost : " + path_cost);
		System.out.println("Number of nodes expanded : " + number_nodes_expanded);
		System.out.println("Maximum tree depth searched " + maximum_tree_depth_searched);
		System.out.println("Maximum size of the frontier " + maximum_size_frontier);
		System.out.println("---------------------------------------------------------");	
	}
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		SEARCH_BUG_TRAP main = new SEARCH_BUG_TRAP();
		main.run();
		System.exit(0);
	}
}