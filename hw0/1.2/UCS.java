import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;

public class UCS {
	
	char[][] maze = new char[100][100];
	char[][] mazeModified;
	int starti, startj, endi, endj, maxx, maxy;
	int[] MAZE_STEPS = {-1, 0, 1};
	String FOLDER = "hw0/1.2/input_files/";
	String FILE = "maze_medium";
	String RESULT = "hw0/1.2/results/";
	
	private class POINT implements Comparable<POINT> {
		int x;
		int y;
		int count;
		double cost;
		
		public POINT(int i, int j, int c, double cst) {
			x = i;
			y = j;
			count = c;
			cost = cst;
		}
		
		@Override
		public int compareTo(POINT obj) {
			return cost - obj.cost > 0 ? 1 : 0;
		}
	}
	
	public void run() throws IOException, InterruptedException
	{
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(FOLDER + FILE + ".txt"));
		String line = null;
		int count = 0;
		
		while ((line = reader.readLine()) != null) {
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
		UCSC12();
		UCSC2();
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
	
	private void UCSC12() throws InterruptedException, FileNotFoundException, UnsupportedEncodingException
	{
		boolean[][] isVisited = new boolean[100][100];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		double path_cost = 0;
		int maximum_size_frontier = 0;

		deepCopyMaze();
		
		Queue<POINT> queue = new PriorityQueue<>();
		queue.add(new POINT(starti, startj, 0, 0));
		
		while (!queue.isEmpty()) {
			if (queue.size() > maximum_size_frontier) { 
				maximum_size_frontier = queue.size();
			}
			
			POINT top = queue.remove();
			
			int x = top.x, y = top.y, count = top.count;
			double cost = top.cost;
			isVisited[x][y] = true;

			if (maximum_tree_depth_searched < count) {
				maximum_tree_depth_searched = count;
			}
			if (maze[x][y] == '.') {
				path_cost = cost;
				break;
			}
			number_nodes_expanded++;
			
			mazeModified[x][y] = '.';
			//clrscr();
			//printMaze(mazeModified);
			//Thread.sleep(300);
	
			for (int i : MAZE_STEPS) {
				for (int j : MAZE_STEPS) {
					if (Math.abs(i) != Math.abs(j) &&
						x + i >= 0 && x + i < maxx &&
						y + j >= 0 && y + j < maxy)
					{
						if (isValid(x + i, y + j, isVisited)) {
							queue.add(new POINT(x + i, y + j, count + 1, cost + Math.pow(0.5, x+i)));
						}
					}
				}
			}
		}
		
		writeToFile("ucsc12");
		
		System.out.println("Uniform cost search cost 1/2^x");
		System.out.println("Path cost : " + path_cost);
		System.out.println("Number of nodes expanded : " + number_nodes_expanded);
		System.out.println("Maximum Tree Depth Searched " + maximum_tree_depth_searched);
		System.out.println("Maximum size of the frontier " + maximum_size_frontier);
		System.out.println("---------------------------------------------------------");	
	}
	
	private void UCSC2() throws InterruptedException, FileNotFoundException, UnsupportedEncodingException
	{
		boolean[][] isVisited = new boolean[100][100];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		double path_cost = 0;
		int maximum_size_frontier = 0;

		deepCopyMaze();
		
		Queue<POINT> queue = new PriorityQueue<>();
		queue.add(new POINT(starti, startj, 0, 0));
		
		while (!queue.isEmpty()) {
			if (queue.size() > maximum_size_frontier) { 
				maximum_size_frontier = queue.size();
			}
			
			POINT top = queue.remove();
			
			int x = top.x, y = top.y, count = top.count;
			double cost = top.cost;
			isVisited[x][y] = true;
			
			if (maximum_tree_depth_searched < count) {
				maximum_tree_depth_searched = count;
			}
			if (maze[x][y] == '.') {
				path_cost = cost;
				break;
			}
			number_nodes_expanded++;
			
			mazeModified[x][y] = '.';
			//clrscr();
			//printMaze(mazeModified);
			//Thread.sleep(300);
			
			for (int i : MAZE_STEPS) {
				for (int j : MAZE_STEPS) {
					if (Math.abs(i) != Math.abs(j) &&
						x + i >= 0 && x + i < maxx &&
						y + j >= 0 && y + j < maxy)
					{
						if (isValid(x + i, y + j, isVisited)) {
							queue.add(new POINT(x + i, y + j, count + 1, cost + Math.pow(2, x+i)));
						}
					}
				}
			}			
		}
		
		writeToFile("ucsc2");
		
		System.out.println("Uniform cost search cost 2^x");
		System.out.println("Path cost : " + path_cost);
		System.out.println("Number of nodes expanded : " + number_nodes_expanded);
		System.out.println("Maximum Tree Depth Searched " + maximum_tree_depth_searched);
		System.out.println("Maximum size of the frontier " + maximum_size_frontier);
		System.out.println("---------------------------------------------------------");	
	}
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		UCS main = new UCS();
		main.run();
		System.exit(0);
	}
}
