import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class SEARCH_MULTIPLE {
	
	char[][] maze = new char[100][100];
	int starti, startj, number_dots;
	
	private class POINT {
		int x;
		int y;
		int count;
		
		public POINT(int i, int j, int c) {
			x = i;
			y = j;
			count = c;
		}
	}
	
	public void run() throws IOException
	{
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader("hw0/1.3/maze_tiny.txt"));
		String line = null;
		int count = 0;
		number_dots = 0;
		
		while ((line = reader.readLine()) != null) {
			for (int i=0; i<line.length(); i++) {
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
		int number_found = 0;
		
		@SuppressWarnings("rawtypes")
		Stack lifo = new Stack();
		lifo.add(new POINT(starti, startj, 0));
		isVisited[starti][startj] = true;
		
		while (!lifo.empty()) {
			POINT top = (POINT) lifo.pop();
			int x = top.x, y = top.y, count = top.count;
			
			if (maximum_tree_depth_searched < count) {
				maximum_tree_depth_searched = count;
			}
			
			if (maze[x][y] == '.')
			{
				number_found++;
				if (number_found == number_dots) {
					break;
				}
			}

			number_nodes_expanded++;
			
			if (!(maze[x - 1][y] == '%' || isVisited[x - 1][y])) {
				lifo.push(new POINT(x - 1, y, count + 1));
				isVisited[x-1][y] = true;
			}
			
			if (!(maze[x][y - 1] == '%' || isVisited[x][y - 1])) {
				lifo.push(new POINT(x, y - 1, count + 1));
				isVisited[x][y-1] = true;
			}
			
			if (!(maze[x][y + 1] == '%' || isVisited[x][y + 1])) {
				lifo.push(new POINT(x, y + 1, count + 1));
				isVisited[x][y+1] = true;
			}
			
			if (!(maze[x + 1][y] == '%' || isVisited[x + 1][y])) {
				lifo.push(new POINT(x + 1, y, count + 1));
				isVisited[x+1][y] = true;
			}
		}
		
		System.out.println("DFS");
		System.out.println("Number of nodes expanded : " + number_nodes_expanded);
		System.out.println("Maximum Tree Depth Searched " + maximum_tree_depth_searched);
		System.out.println("---------------------------------------------------------");
	}
	
	private void BFS()
	{
		boolean[][] isVisited = new boolean[100][100];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		int number_found = 0;
		
		Queue<POINT> fifo = new LinkedList<>();
		fifo.add(new POINT(starti, startj, 0));
		isVisited[starti][startj] = true;
		
		while (!fifo.isEmpty()) {
			POINT top = (POINT) fifo.remove();
			int x = top.x, y = top.y, count = top.count;
			
			if (maximum_tree_depth_searched < count) {
				maximum_tree_depth_searched = count;
			}
			
			if (maze[x][y] == '.')
			{
				number_found++;
				if (number_found == number_dots) {
					break;
				}
			}

			number_nodes_expanded++;

			if (!(maze[x - 1][y] == '%' || isVisited[x - 1][y])) {
				fifo.add(new POINT(x - 1, y, count + 1));
				isVisited[x-1][y] = true;
			}
			
			if (!(maze[x][y - 1] == '%' || isVisited[x][y - 1])) {
				fifo.add(new POINT(x, y - 1, count + 1));
				isVisited[x][y-1] = true;
			}
			
			if (!(maze[x][y + 1] == '%' || isVisited[x][y + 1])) {
				fifo.add(new POINT(x, y + 1, count + 1));
				isVisited[x][y+1] = true;
			}
			
			if (!(maze[x + 1][y] == '%' || isVisited[x + 1][y])) {
				fifo.add(new POINT(x + 1, y, count + 1));
				isVisited[x+1][y] = true;
			}
		}
		
		System.out.println("BFS");
		System.out.println("Number of nodes expanded : " + number_nodes_expanded);
		System.out.println("Maximum Tree Depth Searched " + maximum_tree_depth_searched);
		System.out.println("---------------------------------------------------------");	
	}

	private void GBF()
	{
	
	}

	private void ASTAR()
	{
	
	}
	
	public static void main(String[] args) throws IOException
	{
		SEARCH_MULTIPLE main = new SEARCH_MULTIPLE();
		main.run();
		System.exit(0);
	}
}