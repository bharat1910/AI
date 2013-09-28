import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class SEARCH {
	
	char[][] maze = new char[100][100];
	char[][] mazeModified;
	int starti, startj, endi, endj, maxx, maxy;
	int[] MAZE_STEPS = {-1, 0, 1};
	String FOLDER = "hw0/1.1/input_files/";
	String FILE = "maze_small";
	String RESULT = "hw0/1.1/results/";
	
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

		DFS();
		BFS();
		GBF();
		ASTAR();
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
	
	@SuppressWarnings("unchecked")
	private void DFS() throws InterruptedException, IOException
	{
		boolean[][] isVisited = new boolean[100][100];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		int maximum_size_frontier = 0;
		int path_cost = 0;
		
		deepCopyMaze();
		
		@SuppressWarnings("rawtypes")
		Stack lifo = new Stack();
		lifo.add(new POINT(starti, startj, 0));
		isVisited[starti][startj] = true;
		
		while (!lifo.empty()) {
			if (lifo.size() > maximum_size_frontier) { 
				maximum_size_frontier = lifo.size();
			}
			
			if (lifo.size() > maximum_size_frontier) { 
				maximum_size_frontier = lifo.size();
			}
			
			POINT top = (POINT) lifo.pop();
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
			clrscr();
			printMaze(mazeModified);
			Thread.sleep(300);
			
			for (int i : MAZE_STEPS) {
				for (int j : MAZE_STEPS) {
					if (Math.abs(i) != Math.abs(j) &&
						x + i >= 0 && x + i < maxx &&
						y + j >= 0 && y + j < maxy)
					{
						if (isValid(x + i, y + j, isVisited)) {
							lifo.push(new POINT(x + i, y + j, count + 1));
						}
					}
				}
			}
		}
		
		System.out.println("DFS");
		System.out.println("Path cost : " + path_cost);
		System.out.println("Number of nodes expanded : " + number_nodes_expanded);
		System.out.println("Maximum tree depth searched " + maximum_tree_depth_searched);
		System.out.println("Maximum size of the frontier " + maximum_size_frontier);
		System.out.println("---------------------------------------------------------");
	}
	
	private void BFS() throws InterruptedException
	{
		boolean[][] isVisited = new boolean[100][100];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		int path_cost = 0;
		int maximum_size_frontier = 0;
		
		deepCopyMaze();
		
		Queue<POINT> fifo = new LinkedList<>();
		fifo.add(new POINT(starti, startj, 0));
		isVisited[starti][startj] = true;
		
		while (!fifo.isEmpty()) {
			if (fifo.size() > maximum_size_frontier) { 
				maximum_size_frontier = fifo.size();
			}
			
			POINT top = (POINT) fifo.remove();
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
			clrscr();
			printMaze(mazeModified);
			Thread.sleep(300);
			
			for (int i : MAZE_STEPS) {
				for (int j : MAZE_STEPS) {
					if (Math.abs(i) != Math.abs(j) &&
						x + i >= 0 && x + i < maxx &&
						y + j >= 0 && y + j < maxy)
					{
						if (isValid(x + i, y + j, isVisited)) {
							fifo.add(new POINT(x + i, y + j, count + 1));
						}
					}
				}
			}
		}
		
		System.out.println("BFS");
		System.out.println("Path cost : " + path_cost);
		System.out.println("Number of nodes expanded : " + number_nodes_expanded);
		System.out.println("Maximum tree depth searched " + maximum_tree_depth_searched);
		System.out.println("Maximum size of the frontier " + maximum_size_frontier);
		System.out.println("---------------------------------------------------------");	
	}

	private void GBF() throws InterruptedException
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
			clrscr();
			printMaze(mazeModified);
			Thread.sleep(300);
			
			for (int i : MAZE_STEPS) {
				for (int j : MAZE_STEPS) {
					if (Math.abs(i) != Math.abs(j) &&
						x + i >= 0 && x + i < maxx &&
						y + j >= 0 && y + j < maxy)
					{
						if (isValid(x + i, y + j, isVisited)) {
							queue.add(new POINT(x + i, y + j, count + 1, Math
								.abs(endi - x - i) + Math.abs(endj - y - j)));
						}
					}
				}
			}
		}
		
		System.out.println("GBF");
		System.out.println("Path cost : " + path_cost);
		System.out.println("Number of nodes expanded : " + number_nodes_expanded);
		System.out.println("Maximum tree depth searched " + maximum_tree_depth_searched);
		System.out.println("Maximum size of the frontier " + maximum_size_frontier);
		System.out.println("---------------------------------------------------------");	
	}

	private void ASTAR() throws InterruptedException
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
			clrscr();
			printMaze(mazeModified);
			Thread.sleep(300);
			
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
		
		System.out.println("A*");
		System.out.println("Path cost : " + path_cost);
		System.out.println("Number of nodes expanded : " + number_nodes_expanded);
		System.out.println("Maximum tree depth searched " + maximum_tree_depth_searched);
		System.out.println("Maximum size of the frontier " + maximum_size_frontier);
		System.out.println("---------------------------------------------------------");	
	}
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		SEARCH main = new SEARCH();
		main.run();
		System.exit(0);
	}
}