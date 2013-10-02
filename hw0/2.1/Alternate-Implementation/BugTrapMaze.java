import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

/**
 * 
 * @author Aman's Alien
 * 
 */
public class BugTrapMaze {
	char[][] maze = new char[100][100];
	int starti, startj, endi, endj;
	int holei, holej;
	boolean wall = false;

	public List<POINT> run() throws IOException {
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(
				"C:/work/AI/AI/hw0/2.1/bug_trap_maze.txt"));
		String line = null;
		int count = 0;
		int counter = 0;
		while ((line = reader.readLine()) != null) {
			for (int i = 0; i < line.length(); i++) {
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

		List<POINT> list = null;
		ASTAR();
		return list;
	}

	public List<POINT> runNewAstar() throws IOException {
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(
				"C:/work/AI/AI/hw0/2.1/bug_trap_maze.txt"));
		String line = null;
		int count = 0;
		int counter = 0;
		while ((line = reader.readLine()) != null) {
			for (int i = 0; i < line.length(); i++) {
				maze[count][i] = line.charAt(i);
				if (counter >= 2) {
					if (maze[count][i] == '%' && maze[count][i - 1] == ' '
							&& maze[count][i - 2] == '%') {
						holei = count;
						holej = i - 1;

						// maze[count][i - 1] = '.';

					}
				}
				if (maze[count][i] == 'P') {
					starti = count;
					startj = i;
				}

				if (maze[count][i] == '.') {
					endi = count;
					endj = i;
				}
				counter++;
			}
			counter = 0;
			count++;
		}

		List<POINT> list = null;
		 ASTAR(starti, startj, holei, holej);
		 maze[holei][holej] = ' ';
		 ASTAR(holei, holej, endi, endj);
		//newASTAR(starti, startj, endi, endj);
		return list;
	}

	private boolean isValid(int x, int y, boolean[][] isVisited) {
		if (maze[x][y] == '%') {
			wall = true;
			return false;
		}
		if (isVisited[x][y]) {
			return false;
		} else {
			isVisited[x][y] = true;
			return true;
		}
	}

	private int is(int x, int y, boolean[][] isVisited) {
		if (maze[x][y] == '%') {
			return 0;
		}
		if (isVisited[x][y]) {
			return 1;
		} else {
			isVisited[x][y] = true;
			return 2;
		}
	}

	public List<POINT> ASTAR() {
		return ASTAR(starti, startj, endi, endj);
	}

	public List<POINT> ASTAR(int a, int b, int c, int d) {
		boolean[][] isVisited = new boolean[100][100];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		int path_cost = 0;
		List<POINT> points = new ArrayList<POINT>();
		Queue<POINT> queue = new PriorityQueue<>();

		queue.add(new POINT(a, b, 0, Math.abs(c - a) + Math.abs(d - b)));
		isVisited[a][b] = true;

		while (!queue.isEmpty()) {
			POINT top = queue.remove();

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			points.add(top);
			int x = top.x, y = top.y, count = top.count;
			int count2 = 0;
			maze[x][y] = '.';
			for (int i = 0; i < 21; i++) {
				for (int j = 0; j < 35; j++) {
					if (maze[i][j] != '\0')
						System.out.print(maze[i][j]);
				}
				System.out.println();
			}
			number_nodes_expanded++;
			if (maximum_tree_depth_searched < count) {
				maximum_tree_depth_searched = count;
			}

			if ((x - 1 == c && y == d) || (x + 1 == c && y == d)
					|| (x == c && y - 1 == d) || (x == c && y + 1 == d)) {
				count++;
				path_cost = count;
				if (maximum_tree_depth_searched < count) {
					maximum_tree_depth_searched = count;
				}
				break;
			}

			if (isValid(x - 1, y, isVisited)) {

				queue.add(new POINT(x - 1, y, count + 1, count + 1
						+ Math.abs(c - x + 1) + Math.abs(d - y)));
			}

			if (isValid(x, y - 1, isVisited)) {

				queue.add(new POINT(x, y - 1, count + 1, count + 1
						+ Math.abs(c - x) + Math.abs(d - y + 1)));
			}

			if (isValid(x, y + 1, isVisited)) {

				queue.add(new POINT(x, y + 1, count + 1, count + 1
						+ Math.abs(c - x) + Math.abs(d - y - 1)));
			}

			if (isValid(x + 1, y, isVisited)) {

				queue.add(new POINT(x + 1, y, count + 1, count + 1
						+ Math.abs(c - x - 1) + Math.abs(d - y)));
			}
		}

		System.out.println("A*");
		System.out.println("Path cost : " + path_cost);
		System.out.println("Number of nodes expanded : "
				+ number_nodes_expanded);
		System.out.println("Maximum Tree Depth Searched "
				+ maximum_tree_depth_searched);
		System.out
				.println("---------------------------------------------------------");
		return points;
	}

	public List<POINT> newASTAR(int a, int b, int c, int d) {
		boolean[][] isVisited = new boolean[100][100];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		int path_cost = 0;
		List<POINT> points = new ArrayList<POINT>();
		Queue<POINT> queue = new PriorityQueue<>();

		queue.add(new POINT(a, b, 0, Math.abs(c - a) + Math.abs(d - b)));
		isVisited[a][b] = true;

		while (!queue.isEmpty()) {
			POINT top = queue.remove();

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			points.add(top);
			int x = top.x, y = top.y, count = top.count;
			int count2 = 0;
			maze[x][y] = 'P';
			for (int i = 0; i < 21; i++) {
				for (int j = 0; j < 35; j++) {
					if (maze[i][j] != '\0')
						System.out.print(maze[i][j]);
				}
				System.out.println();
			}
			number_nodes_expanded++;
			if (maximum_tree_depth_searched < count) {
				maximum_tree_depth_searched = count;
			}

			if ((x - 1 == c && y == d) || (x + 1 == c && y == d)
					|| (x == c && y - 1 == d) || (x == c && y + 1 == d)) {
				count++;
				path_cost = count;
				if (maximum_tree_depth_searched < count) {
					maximum_tree_depth_searched = count;
				}
				break;
			}

			if (isValid(x - 1, y, isVisited)) {
				int h = count + 1 + Math.abs(c - x + 1) + Math.abs(d - y);
				if (is(x - 1, y, isVisited) == 0) {
					h = h / 2;
				}

				queue.add(new POINT(x - 1, y, count + 1, h));
			}

			if (isValid(x, y - 1, isVisited)) {
				int h = count + 1 + Math.abs(c - x) + Math.abs(d - y + 1);
				if (is(x, y - 1, isVisited) == 0) {
					h = h / 2;
				}
				queue.add(new POINT(x, y - 1, count + 1, h));
			}

			if (isValid(x, y + 1, isVisited)) {
				int h = count + 1 + Math.abs(c - x) + Math.abs(d - y - 1);
				if (is(x, y + 1, isVisited) == 0) {
					h = h / 2;
				}
				queue.add(new POINT(x, y + 1, count + 1, h));
			}

			if (isValid(x + 1, y, isVisited)) {
				int h = count + 1 + Math.abs(c - x - 1) + Math.abs(d - y);
				if (is(x + 1, y, isVisited) == 0) {
					h = h / 2;
				}
				queue.add(new POINT(x + 1, y, count + 1, h));
			}
		}

		System.out.println("A*");
		System.out.println("Path cost : " + path_cost);
		System.out.println("Number of nodes expanded : "
				+ number_nodes_expanded);
		System.out.println("Maximum Tree Depth Searched "
				+ maximum_tree_depth_searched);
		System.out
				.println("---------------------------------------------------------");
		return points;
	}

	public List<POINT> modifiedAstar() {
		boolean[][] isVisited = new boolean[100][100];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		int path_cost = 0;
		List<POINT> points = new ArrayList<POINT>();
		Queue<POINT> queue = new PriorityQueue<>();
		queue.add(new POINT(starti, startj, 0, Math.abs(endi - starti)
				+ Math.abs(endj - startj)));
		isVisited[starti][startj] = true;
		boolean wall = false;

		while (!queue.isEmpty()) {
			POINT top = queue.remove();
			try {

				// Runtime.getRuntime().exec("cls");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			points.add(top);
			int x = top.x, y = top.y, count = top.count;
			int count2 = 0;
			maze[x][y] = 'P';
			for (int i = 0; i < 21; i++) {
				for (int j = 0; j < 35; j++) {
					if (maze[i][j] != '\0')
						System.out.print(maze[i][j]);
				}
				System.out.println();
			}
			number_nodes_expanded++;
			if (maximum_tree_depth_searched < count) {
				maximum_tree_depth_searched = count;
			}

			if (maze[x - 1][y] == '.' || maze[x + 1][y] == '.'
					|| maze[x][y - 1] == '.' || maze[x][y + 1] == '.') {
				count++;
				path_cost = count;
				if (maximum_tree_depth_searched < count) {
					maximum_tree_depth_searched = count;
				}
				break;
			}
			// if (!wall) {
			if (isValid(x - 1, y, isVisited)) {
				queue.add(new POINT(x - 1, y, count + 1, count + 1
						+ Math.abs(endi - x + 1) + Math.abs(endj - y)));
			}

			if (isValid(x, y - 1, isVisited)) {
				queue.add(new POINT(x, y - 1, count + 1, count + 1
						+ Math.abs(endi - x) + Math.abs(endj - y + 1)));
			}

			if (isValid(x, y + 1, isVisited)) {
				queue.add(new POINT(x, y + 1, count + 1, count + 1
						+ Math.abs(endi - x) + Math.abs(endj - y - 1)));
			}

			if (isValid(x + 1, y, isVisited)) {
				queue.add(new POINT(x + 1, y, count + 1, count + 1
						+ Math.abs(endi - x - 1) + Math.abs(endj - y)));
			}
			// } else {
			//
			// }
		}

		// System.out.println("A*");
		// System.out.println("Path cost : " + path_cost);
		// System.out.println("Number of nodes expanded : "
		// + number_nodes_expanded);
		// System.out.println("Maximum Tree Depth Searched "
		// + maximum_tree_depth_searched);
		// System.out
		// .println("---------------------------------------------------------");
		return points;
	}

	@SuppressWarnings("unchecked")
	private void DFS() {
		boolean[][] isVisited = new boolean[100][100];
		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		int path_cost = 0;

		@SuppressWarnings("rawtypes")
		Stack lifo = new Stack();
		lifo.add(new POINT(starti, startj, 0));
		isVisited[starti][startj] = true;
		Result result = null;
		boolean c = false;
		int count1 = 0;
		while (!lifo.empty()) {
			try {

				// Runtime.getRuntime().exec("cls");
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			POINT top = (POINT) lifo.pop();
			int x = top.x, y = top.y, count = top.count;
			maze[x][y] = 'P';
			for (int i = 0; i < 21; i++) {
				for (int j = 0; j < 35; j++) {
					if (maze[i][j] != '\0')
						System.out.print(maze[i][j]);
				}
				System.out.println();
			}
			if (maze[x][y - 1] == '%' && maze[x][y + 1] == '%') {
				wall = false;
				count1++;
				path_cost=count;
				result = startAstar(x - 1, y, isVisited);
				break;

			}
			number_nodes_expanded++;
			if (maximum_tree_depth_searched < count) {
				maximum_tree_depth_searched = count;
			}

			if (maze[x - 1][y] == '.' || maze[x + 1][y] == '.'
					|| maze[x][y - 1] == '.' || maze[x][y + 1] == '.') {
				count++;
				path_cost = count;
				if (maximum_tree_depth_searched < count) {
					maximum_tree_depth_searched = count;
				}
				break;
			}

			if (!wall || count1 != 0) {
				if (isValid(x - 1, y, isVisited)) {
					lifo.push(new POINT(x - 1, y, count + 1));
				}

				if (isValid(x, y - 1, isVisited)) {
					lifo.push(new POINT(x, y - 1, count + 1));
				}

				if (isValid(x, y + 1, isVisited)) {
					lifo.push(new POINT(x, y + 1, count + 1));
				}

				if (isValid(x + 1, y, isVisited)) {
					lifo.push(new POINT(x + 1, y, count + 1));
				}
			} else if (count1 == 0) {
				lifo.clear();
				if (isValid(x - 1, y, isVisited)) {
					lifo.push(new POINT(x - 1, y, count + 1));
				} else if (isValid(x, y - 1, isVisited)) {
					lifo.push(new POINT(x, y - 1, count + 1));
				} else if (isValid(x, y + 1, isVisited)) {
					lifo.push(new POINT(x, y + 1, count + 1));
				} else if (isValid(x + 1, y, isVisited)) {
					lifo.push(new POINT(x + 1, y, count + 1));
				}
			}
		}

		System.out.println("DFS");
		System.out.println("Path cost : " + (result.path_cost + path_cost));
		System.out.println("Number of nodes expanded : "
				+ (result.number_of_nodes + number_nodes_expanded));
		System.out.println("Maximum Tree Depth Searched "
				+ (result.maximum_tree_depth + maximum_tree_depth_searched));
		System.out
				.println("---------------------------------------------------------");
	}

	private Result startAstar(int x, int y, boolean[][] isVisited) {

		int number_nodes_expanded = 0;
		int maximum_tree_depth_searched = 0;
		int path_cost = 0;
		List<POINT> points = new ArrayList<POINT>();
		Queue<POINT> queue = new PriorityQueue<>();
		queue.add(new POINT(x, y, 0, Math.abs(endi - x) + Math.abs(endj - y)));
		isVisited[x][y] = true;
		boolean wall = false;

		while (!queue.isEmpty()) {
			POINT top = queue.remove();
			try {

				// Runtime.getRuntime().exec("cls");
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			points.add(top);
			int x1 = top.x, y1 = top.y, count = top.count;
			int count2 = 0;
			maze[x1][y1] = 'P';
			for (int i = 0; i < 21; i++) {
				for (int j = 0; j < 35; j++) {
					if (maze[i][j] != '\0')
						System.out.print(maze[i][j]);
				}
				System.out.println();
			}
			number_nodes_expanded++;
			if (maximum_tree_depth_searched < count) {
				maximum_tree_depth_searched = count;
			}

			if (maze[x1 - 1][y1] == '.' || maze[x1 + 1][y1] == '.'
					|| maze[x1][y1 - 1] == '.' || maze[x1][y1 + 1] == '.') {
				count++;
				path_cost = count;
				if (maximum_tree_depth_searched < count) {
					maximum_tree_depth_searched = count;
				}
				break;
			}
			// if (!wall) {
			if (isValid(x1 - 1, y1, isVisited)) {
				queue.add(new POINT(x1 - 1, y1, count + 1, count + 1
						+ Math.abs(endi - x1 + 1) + Math.abs(endj - y1)));
			}

			if (isValid(x1, y1 - 1, isVisited)) {
				queue.add(new POINT(x1, y1 - 1, count + 1, count + 1
						+ Math.abs(endi - x1) + Math.abs(endj - y1 + 1)));
			}

			if (isValid(x1, y1 + 1, isVisited)) {
				queue.add(new POINT(x1, y1 + 1, count + 1, count + 1
						+ Math.abs(endi - x1) + Math.abs(endj - y1 - 1)));
			}

			if (isValid(x1 + 1, y1, isVisited)) {
				queue.add(new POINT(x1 + 1, y1, count + 1, count + 1
						+ Math.abs(endi - x1 - 1) + Math.abs(endj - y1)));
			}
		}
		Result result = new Result();
		result.maximum_tree_depth = maximum_tree_depth_searched;
		result.number_of_nodes = number_nodes_expanded;
		result.path_cost = path_cost;
		return result;

	}

	public static void main(String[] args) throws IOException {
		BugTrapMaze main = new BugTrapMaze();
		main.run();
		System.exit(0);
	}
}
