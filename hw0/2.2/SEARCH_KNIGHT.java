import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class SEARCH_KNIGHT {
	
	char[][] maze = new char[100][100];
	int starti, startj, endi, endj, maxx, maxy;
	int[] KNIGHT_STEPS = {-2, -1, 1, 2};
	
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
	
	public void run() throws IOException
	{
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader("hw0/1.1/maze_small.txt"));
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
	
	private boolean checkKnightStepsForGoal(int x, int y)
	{
		for (int i : KNIGHT_STEPS) {
			for (int j : KNIGHT_STEPS) {
				if (Math.abs(i) != Math.abs(j) &&
					x+i >= 0 && x+i < maxx &&
					y+j >= 0 && y+j < maxy)
				{
					if (maze[x + i][y + j] == '.') return true;
				}
			}
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private void DFS()
	{
		boolean[][] isVisited = new boolean[100][100];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		int path_cost = 0;
		
		@SuppressWarnings("rawtypes")
		Stack lifo = new Stack();
		lifo.add(new POINT(starti, startj, 0));
		isVisited[starti][startj] = true;
		
		while (!lifo.empty()) {
			POINT top = (POINT) lifo.pop();
			int x = top.x, y = top.y, count = top.count;
			
			number_nodes_expanded++;
			if (maximum_tree_depth_searched < count) {
				maximum_tree_depth_searched = count;
			}
			
			if (checkKnightStepsForGoal(x, y))
			{
				count++;
				path_cost = count;
				if (maximum_tree_depth_searched < count) {
					maximum_tree_depth_searched = count;
				}
				break;
			}
			
			for (int i : KNIGHT_STEPS) {
				for (int j : KNIGHT_STEPS) {
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
		System.out.println("Maximum Tree Depth Searched " + maximum_tree_depth_searched);
		System.out.println("---------------------------------------------------------");
	}
	
	private void BFS()
	{
		boolean[][] isVisited = new boolean[100][100];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		int path_cost = 0;
		
		Queue<POINT> fifo = new LinkedList<>();
		fifo.add(new POINT(starti, startj, 0));
		isVisited[starti][startj] = true;
		
		while (!fifo.isEmpty()) {
			POINT top = (POINT) fifo.remove();
			int x = top.x, y = top.y, count = top.count;
			
			number_nodes_expanded++;
			if (maximum_tree_depth_searched < count) {
				maximum_tree_depth_searched = count;
			}
			
			if (checkKnightStepsForGoal(x, y))
			{
				count++;
				path_cost = count;
				if (maximum_tree_depth_searched < count) {
					maximum_tree_depth_searched = count;
				}
				break;
			}
			
			for (int i : KNIGHT_STEPS) {
				for (int j : KNIGHT_STEPS) {
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
		System.out.println("Maximum Tree Depth Searched " + maximum_tree_depth_searched);
		System.out.println("---------------------------------------------------------");	
	}

	private void GBF()
	{
		boolean[][] isVisited = new boolean[100][100];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		int path_cost = 0;

		Queue<POINT> queue = new PriorityQueue<>();
		queue.add(new POINT(starti, startj, 0, Math.abs(endi - starti) + Math.abs(endj - startj)));
		isVisited[starti][startj] = true;
		
		while (!queue.isEmpty()) {
			POINT top = queue.remove();
			
			int x = top.x, y = top.y, count = top.count;
			
			number_nodes_expanded++;
			if (maximum_tree_depth_searched < count) {
				maximum_tree_depth_searched = count;
			}
			
			if (checkKnightStepsForGoal(x, y))
			{
				count++;
				path_cost = count;
				if (maximum_tree_depth_searched < count) {
					maximum_tree_depth_searched = count;
				}
				break;
			}
			
			for (int i : KNIGHT_STEPS) {
				for (int j : KNIGHT_STEPS) {
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
		System.out.println("Maximum Tree Depth Searched " + maximum_tree_depth_searched);
		System.out.println("---------------------------------------------------------");	
	}

	private void ASTAR()
	{
		boolean[][] isVisited = new boolean[100][100];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		int path_cost = 0;

		Queue<POINT> queue = new PriorityQueue<>();
		queue.add(new POINT(starti, startj, 0, Math.abs(endi - starti) + Math.abs(endj - startj)));
		isVisited[starti][startj] = true;
		
		while (!queue.isEmpty()) {
			POINT top = queue.remove();
			
			int x = top.x, y = top.y, count = top.count;
			
			number_nodes_expanded++;
			if (maximum_tree_depth_searched < count) {
				maximum_tree_depth_searched = count;
			}
			
			if (checkKnightStepsForGoal(x, y))
			{
				count++;
				path_cost = count;
				if (maximum_tree_depth_searched < count) {
					maximum_tree_depth_searched = count;
				}
				break;
			}
			
			for (int i : KNIGHT_STEPS) {
				for (int j : KNIGHT_STEPS) {
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
		System.out.println("Maximum Tree Depth Searched " + maximum_tree_depth_searched);
		System.out.println("---------------------------------------------------------");	
	}
	
	public static void main(String[] args) throws IOException
	{
		SEARCH_KNIGHT main = new SEARCH_KNIGHT();
		main.run();
		System.exit(0);
	}
}