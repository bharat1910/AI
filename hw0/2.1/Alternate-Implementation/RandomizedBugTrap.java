import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

/**
 * 
 * @author Aman Grover
 * 
 */
public class RandomizedBugTrap {
	char[][] maze = new char[100][100];
	int starti, startj, endi, endj;
	boolean wall = false;
	int number_of_nodes_expanded;
	int path_cost;
	boolean[][] isVisited = new boolean[100][100];
	Result result = new Result();

	public void run() throws IOException, InterruptedException {
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(
				"C:/work/AI/AI/hw0/2.1/bug_trap_maze.txt"));
		String line = null;
		int count = 0;

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

		randomizedBug();
		// performAstar(starti, startj);

	}

	private void randomizedBug() throws InterruptedException {
		Random random = new Random();
		int rand = random.nextInt(4);
		Random random2 = new Random();
		int rand2 = random2.nextInt(50);
		System.out.println("Random steps to be taken are : " + rand2);
		switch (rand) {
		case 0:
			System.out.println("Going North .....................");
			goNorth(rand2);

			break;
		case 1:
			System.out.println("Going South .....................");
			goSouth(rand2);

			break;
		case 2:
			System.out.println("Going East .....................");
			goEast(rand2);
			break;
		case 3:
			System.out.println("Going West .....................");
			goWest(rand2);
			break;

		default:
			break;
		}

	}

	private void goWest(int rand2) throws InterruptedException {
		int i = 0;
		int x = starti;
		int y = startj;
		while (i < rand2 && maze[x][y - 1] != '%') {
			y = y - 1;
			number_of_nodes_expanded++;
			isVisited[x][y] = true;
			path_cost++;
			// Thread.sleep(100);
			maze[x][y] = 'P';
			// printMaze();
			i++;
		}
		System.out.println("Start Performing A star");
		System.out.println("Path cos : " + path_cost);
		performAstar(x, y);

	}

	private void goEast(int rand2) throws InterruptedException {
		int i = 0;
		int x = starti;
		int y = startj;
		while (i < rand2 && maze[x][y + 1] != '%') {
			y = y + 1;
			number_of_nodes_expanded++;
			isVisited[x][y] = true;
			path_cost++;
			// Thread.sleep(100);
			maze[x][y] = 'P';
			// printMaze();
			i++;
		}
		System.out.println("Start Performing A star");
		System.out.println("Path cos : " + path_cost);
		performAstar(x, y);

	}

	private void goSouth(int rand2) throws InterruptedException {
		int i = 0;
		int x = starti;
		int y = startj;
		while (i < rand2 && maze[x + 1][y] != '%') {
			x = x + 1;
			number_of_nodes_expanded++;
			isVisited[x][y] = true;
			path_cost++;
			// Thread.sleep(100);
			maze[x][y] = 'P';
			// printMaze();
			i++;
		}
		System.out.println("Start Performing A star");
		System.out.println("Path cos : " + path_cost);
		performAstar(x, y);

	}

	private void goNorth(int rand2) throws InterruptedException {
		int i = 0;
		int x = starti;
		int y = startj;
		while (i < rand2 && maze[x - 1][y] != '%') {
			x = x - 1;
			number_of_nodes_expanded++;
			isVisited[x][y] = true;
			path_cost++;
			i++;
			// Thread.sleep(100);
			maze[x][y] = 'P';
			// printMaze();
		}
		System.out.println("Start Performing A star");
		System.out.println("Path cos : " + path_cost);
		performAstar(x, y);
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

	private void performAstar(int a, int b) throws InterruptedException {
		int path_cost = 0;
		List<POINT> points = new ArrayList<POINT>();
		Queue<POINT> queue = new PriorityQueue<>();
		int maximum_tree_depth_searched = 0;
		queue.add(new POINT(a, b, path_cost, path_cost + Math.abs(endi - a)
				+ Math.abs(endj - b)));
		isVisited[a][b] = true;

		while (!queue.isEmpty()) {
			POINT top = queue.remove();

			// Thread.sleep(100);

			points.add(top);
			int x = top.x, y = top.y, count = top.count;
			int count2 = 0;
			maze[x][y] = 'P';
			// printMaze()
			number_of_nodes_expanded++;
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
		}
		result.path_cost = path_cost;
		result.number_of_nodes = number_of_nodes_expanded;
		result.maximum_tree_depth = maximum_tree_depth_searched;
		

	}

	void printMaze() {
		for (int i = 0; i < 21; i++) {
			for (int j = 0; j < 35; j++) {
				if (maze[i][j] != '\0')
					System.out.print(maze[i][j]);
			}
			System.out.println();
		}
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {
		int total_path_cost = 0;
		int total_number_of_nodes = 0;
		int total_tree_depth = 0;
		int total = 1000;
		List<Result> results = new ArrayList<Result>();
		for (int i = 0; i < total; i++) {
			RandomizedBugTrap bugTrap = new RandomizedBugTrap();
			bugTrap.run();
			results.add(bugTrap.result);
		}
		for (Result result : results) {
			total_path_cost += result.path_cost;
			total_number_of_nodes += result.number_of_nodes;
			total_tree_depth += result.maximum_tree_depth;
		}

		System.out.println("A*");
		System.out.println("Path cost : " + (total_path_cost / total));
		System.out.println("Number of nodes expanded : "
				+ (total_number_of_nodes / total));
		System.out.println("Maximum Tree Depth Searched "
				+ (total_tree_depth / total));
		System.out
				.println("---------------------------------------------------------");

	}

}
