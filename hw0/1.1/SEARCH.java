import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class SEARCH {
	
	char[][] maze = new char[100][100];
	int starti, startj, endi, endj;
	
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
		BufferedReader reader = new BufferedReader(new FileReader("/home/anusha/Prac/AI/maze.txt"));
		String line = null;
		int count = 0;
		
		while ((line = reader.readLine()) != null) {
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
		
		DFS();
		BFS();
		GBF();
		ASTAR();
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
		
		while (!lifo.empty()) {
			POINT top = (POINT) lifo.pop();
			int x = top.x, y = top.y, count = top.count;
			isVisited[x][y] = true;
			
			number_nodes_expanded++;
			if (maximum_tree_depth_searched < count) {
				maximum_tree_depth_searched = count;
			}
			
			if (maze[x - 1][y] == '.' ||
				maze[x + 1][y] == '.' ||
				maze[x][y - 1] == '.' ||
				maze[x][y + 1] == '.')
			{
				count++;
				path_cost = count;
				if (maximum_tree_depth_searched < count) {
					maximum_tree_depth_searched = count;
				}
				break;
			}
			
			if (!(maze[x - 1][y] == '%' || isVisited[x - 1][y])) {
				lifo.push(new POINT(x - 1, y, count + 1));
			}
			
			if (!(maze[x][y - 1] == '%' || isVisited[x][y - 1])) {
				lifo.push(new POINT(x, y - 1, count + 1));
			}
			
			if (!(maze[x][y + 1] == '%' || isVisited[x][y + 1])) {
				lifo.push(new POINT(x, y + 1, count + 1));
			}
			
			if (!(maze[x + 1][y] == '%' || isVisited[x + 1][y])) {
				lifo.push(new POINT(x + 1, y, count + 1));
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
		
		while (!fifo.isEmpty()) {
			POINT top = (POINT) fifo.remove();
			int x = top.x, y = top.y, count = top.count;
			isVisited[x][y] = true;
			
			number_nodes_expanded++;
			if (maximum_tree_depth_searched < count) {
				maximum_tree_depth_searched = count;
			}
			
			if (maze[x - 1][y] == '.' ||
				maze[x + 1][y] == '.' ||
				maze[x][y - 1] == '.' ||
				maze[x][y + 1] == '.')
			{
				count++;
				path_cost = count;
				if (maximum_tree_depth_searched < count) {
					maximum_tree_depth_searched = count;
				}
				break;
			}
			
			if (!(maze[x - 1][y] == '%' || isVisited[x - 1][y])) {
				fifo.add(new POINT(x - 1, y, count + 1));
			}
			
			if (!(maze[x][y - 1] == '%' || isVisited[x][y - 1])) {
				fifo.add(new POINT(x, y - 1, count + 1));
			}
			
			if (!(maze[x][y + 1] == '%' || isVisited[x][y + 1])) {
				fifo.add(new POINT(x, y + 1, count + 1));
			}
			
			if (!(maze[x + 1][y] == '%' || isVisited[x + 1][y])) {
				fifo.add(new POINT(x + 1, y, count + 1));
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
		
		while (!queue.isEmpty()) {
			POINT top = queue.remove();
			
			int x = top.x, y = top.y, count = top.count;
			isVisited[x][y] = true;
			
			number_nodes_expanded++;
			if (maximum_tree_depth_searched < count) {
				maximum_tree_depth_searched = count;
			}
			
			if (maze[x - 1][y] == '.' ||
				maze[x + 1][y] == '.' ||
				maze[x][y - 1] == '.' ||
				maze[x][y + 1] == '.')
			{
				count++;
				path_cost = count;
				if (maximum_tree_depth_searched < count) {
					maximum_tree_depth_searched = count;
				}
				break;
			}
			
			if (!(maze[x - 1][y] == '%' || isVisited[x - 1][y])) {
				queue.add(new POINT(x - 1, y, count + 1, Math.abs(endi - x + 1) + Math.abs(endj - y)));
			}
			
			if (!(maze[x][y - 1] == '%' || isVisited[x][y - 1])) {
				queue.add(new POINT(x, y - 1, count + 1, Math.abs(endi - x) + Math.abs(endj - y + 1)));
			}
			
			if (!(maze[x][y + 1] == '%' || isVisited[x][y + 1])) {
				queue.add(new POINT(x, y + 1, count + 1, Math.abs(endi - x) + Math.abs(endj - y - 1)));
			}
			
			if (!(maze[x + 1][y] == '%' || isVisited[x + 1][y])) {
				queue.add(new POINT(x + 1, y, count + 1, Math.abs(endi - x - 1) + Math.abs(endj - y)));
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
		
		while (!queue.isEmpty()) {
			POINT top = queue.remove();
			
			int x = top.x, y = top.y, count = top.count;
			isVisited[x][y] = true;
			
			number_nodes_expanded++;
			if (maximum_tree_depth_searched < count) {
				maximum_tree_depth_searched = count;
			}
			
			if (maze[x - 1][y] == '.' ||
				maze[x + 1][y] == '.' ||
				maze[x][y - 1] == '.' ||
				maze[x][y + 1] == '.')
			{
				count++;
				path_cost = count;
				if (maximum_tree_depth_searched < count) {
					maximum_tree_depth_searched = count;
				}
				break;
			}
			
			if (!(maze[x - 1][y] == '%' || isVisited[x - 1][y])) {
				queue.add(new POINT(x - 1, y, count + 1, count + 1 + Math.abs(endi - x + 1) + Math.abs(endj - y)));
			}
			
			if (!(maze[x][y - 1] == '%' || isVisited[x][y - 1])) {
				queue.add(new POINT(x, y - 1, count + 1, count + 1 + Math.abs(endi - x) + Math.abs(endj - y + 1)));
			}
			
			if (!(maze[x][y + 1] == '%' || isVisited[x][y + 1])) {
				queue.add(new POINT(x, y + 1, count + 1, count + 1 + Math.abs(endi - x) + Math.abs(endj - y - 1)));
			}
			
			if (!(maze[x + 1][y] == '%' || isVisited[x + 1][y])) {
				queue.add(new POINT(x + 1, y, count + 1, count + 1 + Math.abs(endi - x - 1) + Math.abs(endj - y)));
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
		SEARCH main = new SEARCH();
		main.run();
		System.exit(0);
	}
}
