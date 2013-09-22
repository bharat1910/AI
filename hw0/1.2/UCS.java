import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Queue;

public class UCS {
	
	char[][] maze = new char[100][100];
	int starti, startj, endi, endj;
	
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
	
	public void run() throws IOException
	{
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader("hw0/1.2/maze_medium.txt"));
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
		
		UCSC12();
		UCSC2();
	}

	private void UCSC12()
	{
		boolean[][] isVisited = new boolean[100][100];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		double path_cost = 0;

		Queue<POINT> queue = new PriorityQueue<>();
		queue.add(new POINT(starti, startj, 0, 0));
		
		while (!queue.isEmpty()) {
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
			
			if (!(maze[x - 1][y] == '%' || isVisited[x - 1][y])) {
				queue.add(new POINT(x - 1, y, count + 1, cost + Math.pow(0.5, x-1)));
			}
			
			if (!(maze[x][y - 1] == '%' || isVisited[x][y - 1])) {
				queue.add(new POINT(x, y - 1, count + 1, cost + Math.pow(0.5, x)));
			}
			
			if (!(maze[x][y + 1] == '%' || isVisited[x][y + 1])) {
				queue.add(new POINT(x, y + 1, count + 1, cost + Math.pow(0.5, x)));
			}
			
			if (!(maze[x + 1][y] == '%' || isVisited[x + 1][y])) {
				queue.add(new POINT(x + 1, y, count + 1, cost + Math.pow(0.5, x+1)));
			}
		}
		
		System.out.println("Uniform cost search cost 1/2^x");
		System.out.println("Path cost : " + path_cost);
		System.out.println("Number of nodes expanded : " + number_nodes_expanded);
		System.out.println("Maximum Tree Depth Searched " + maximum_tree_depth_searched);
		System.out.println("---------------------------------------------------------");	
	}
	
	private void UCSC2()
	{
		boolean[][] isVisited = new boolean[100][100];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		double path_cost = 0;

		Queue<POINT> queue = new PriorityQueue<>();
		queue.add(new POINT(starti, startj, 0, 0));
		
		while (!queue.isEmpty()) {
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
			
			if (!(maze[x - 1][y] == '%' || isVisited[x - 1][y])) {
				queue.add(new POINT(x - 1, y, count + 1, cost + Math.pow(2, x-1)));
			}
			
			if (!(maze[x][y - 1] == '%' || isVisited[x][y - 1])) {
				queue.add(new POINT(x, y - 1, count + 1, cost + Math.pow(2, x)));
			}
			
			if (!(maze[x][y + 1] == '%' || isVisited[x][y + 1])) {
				queue.add(new POINT(x, y + 1, count + 1, cost + Math.pow(2, x)));
			}
			
			if (!(maze[x + 1][y] == '%' || isVisited[x + 1][y])) {
				queue.add(new POINT(x + 1, y, count + 1, cost + Math.pow(2, x+1)));
			}
		}
		
		System.out.println("Uniform cost search cost 2^x");
		System.out.println("Path cost : " + path_cost);
		System.out.println("Number of nodes expanded : " + number_nodes_expanded);
		System.out.println("Maximum Tree Depth Searched " + maximum_tree_depth_searched);
		System.out.println("---------------------------------------------------------");	
	}
	
	public static void main(String[] args) throws IOException
	{
		UCS main = new UCS();
		main.run();
		System.exit(0);
	}
}