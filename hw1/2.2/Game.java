import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Game
{
	int N_VAL = 6;
	int[][] board;
	int[] moves = {1, 0, -1};
	
	private Boolean[][] deepCopy(Boolean[][] isVisted)
	{
		Boolean[][] isVisitedCopy = new Boolean[N_VAL][N_VAL];
		
		for (int i=0; i<N_VAL; i++) {
			for (int j=0; j<N_VAL; j++) {
				isVisitedCopy[i][j] = isVisted[i][j];
			}
		}
		
		return isVisitedCopy;
	}
	
	private int diff(Boolean[][] isVisited)
	{
		int sumBlue =0, sumGreen = 0;
		
		for (int i=0; i<N_VAL; i++) {
			for (int j=0; j<N_VAL; j++) {
				if (isVisited[i][j] == null) {
					continue;
				}
				else if (isVisited[i][j]) {
					sumBlue += board[i][j];
				}
				else {
					sumGreen += board[i][j];
				}
			}
		}
		
		return sumBlue - sumGreen;
	}
	
	private boolean hasAdjacent(Boolean[][] isVisited, int i, int j, boolean player)
	{
		for (int u : moves) {
			for (int v : moves) {
				if (Math.abs(u) != Math.abs(v) &&
					i + u >= 0 &&
					i + u < N_VAL &&
					j + v >= 0 &&
					j + v < N_VAL && 
					isVisited[i+u][j+v] != null &&
					isVisited[i+u][j+v] == player) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean turnAdjacent(Boolean[][] isVisited, int i, int j, boolean player)
	{
		for (int u : moves) {
			for (int v : moves) {
				if (Math.abs(u) != Math.abs(v) &&
					i + u >= 0 &&
					i + u < N_VAL &&
					j + v >= 0 &&
					j + v < N_VAL &&
					isVisited[i+u][j+v] != null &&
					isVisited[i+u][j+v] == !player) {
					isVisited[i+u][j+v] = player;
				}
			}
		}
		
		return false;
	}
	
	private boolean allVisited(Boolean[][] isVisited)
	{
		for (int i=0; i<N_VAL; i++) {
			for (int j=0; j<N_VAL; j++) {
				if (isVisited[i][j] == null) {
					return false;
				}
			}
		}
		return true;
	}

	private int evaluationFuntionAlphaBeta(Boolean[][] isVisited, int depth,
			boolean player, int max, int min) {
		if (depth == 0 || allVisited(isVisited)) {
			return diff(isVisited);
		}

		int value = -1;

		for (int i = 0; i < N_VAL; i++) {
			for (int j = 0; j < N_VAL; j++) {
				if (isVisited[i][j] == null) {
					Boolean[][] isVisitedCopy = deepCopy(isVisited);
					isVisitedCopy[i][j] = player;
					if (hasAdjacent(isVisitedCopy, i, j, player)) {
						turnAdjacent(isVisitedCopy, i, j, player);
					}

					value = evaluationFuntionAlphaBeta(isVisitedCopy, depth - 1, !player, max, min);
					if (player && value > max) {
						max = value;
					} else if (!player && value < min) {
						min = value;
					}
					
					if (player && value > min) {
						return value;
					}
					if (!player && value < max) {
						return value;
					}
				}
			}
		}

		if (player) {
			return max;
		}
		else {
			return min;
		}
	}
	
	private int evaluationFuntion(Boolean[][] isVisited,
								   int depth,
								   boolean player)
	{
		if (depth == 0 || allVisited(isVisited)) {
			return diff(isVisited);
		}
		
		int value, temp;
		if (player) {
			value = Integer.MIN_VALUE;
		}
		else {
			value = Integer.MAX_VALUE;
		}
		
		for (int i=0; i<N_VAL; i++) {
			for (int j=0; j<N_VAL; j++) {
				if (isVisited[i][j] == null) {
					Boolean[][] isVisitedCopy = deepCopy(isVisited); 
					isVisitedCopy[i][j] = player;
					if (hasAdjacent(isVisitedCopy, i, j, player)) {
						turnAdjacent(isVisitedCopy, i, j, player);
					}
					
					temp = evaluationFuntion(isVisitedCopy, depth - 1, !player);
					if (player && temp > value) {
						value = temp;
					}
					else if (!player && temp < value) {
						value = temp;
					}
				}
			}
		}
		
		return value;
	}
	
	private void makeMove(Boolean[][] isVisited, int depth,
			boolean player)
	{
		if (depth == N_VAL * N_VAL) {
			System.out.println(diff(isVisited));
			if (diff(isVisited) == 0) {
				System.out.println("Game drawn");
			}
			else if (diff(isVisited) > 0) {
				System.out.println("Blue wins");
			}
			else {
				System.out.println("Green Wins");
			}
			return;
		}
		
		int value, temp, movei = -1, movej = -1;
		if (player) {
			value = Integer.MIN_VALUE;
		} else {
			value = Integer.MAX_VALUE;
		}

		for (int i = 0; i < N_VAL; i++) {
			for (int j = 0; j < N_VAL; j++) {
				if (isVisited[i][j] == null) {
					Boolean[][] isVisitedCopy = deepCopy(isVisited);
					isVisitedCopy[i][j] = player;
					if (hasAdjacent(isVisitedCopy, i, j, player)) {
						turnAdjacent(isVisitedCopy, i, j, player);
					}
					
					temp = evaluationFuntionAlphaBeta(isVisitedCopy, 3, !player, Integer.MIN_VALUE, Integer.MAX_VALUE);

					if (player && temp > value) {
						movei = i;
						movej = j;
						value = temp;
					} else if (!player && temp < value) {
						movei = i;
						movej = j;
						value = temp;
					}
				}
			}
		}
		
		isVisited[movei][movej] = player;
		System.out.println(movei + " " + movej + " " + value);
		makeMove(deepCopy(isVisited), depth + 1, !player);
	}
	
	
	private void makeMoveAdditional(Boolean[][] isVisited, int depth,
			boolean player)
	{
		if (depth == N_VAL * N_VAL) {
			System.out.println(diff(isVisited));
			if (diff(isVisited) == 0) {
				System.out.println("Game drawn");
			}
			else if (diff(isVisited) > 0) {
				System.out.println("Blue wins");
			}
			else {
				System.out.println("Green Wins");
			}
			return;
		}
		
		int value, temp, movei = -1, movej = -1;
		if (player) {
			value = Integer.MIN_VALUE;
		} else {
			value = Integer.MAX_VALUE;
		}

		for (int i = 0; i < N_VAL; i++) {
			for (int j = 0; j < N_VAL; j++) {
				if (isVisited[i][j] == null) {
					Boolean[][] isVisitedCopy = deepCopy(isVisited);
					isVisitedCopy[i][j] = player;
					if (hasAdjacent(isVisitedCopy, i, j, player)) {
						turnAdjacent(isVisitedCopy, i, j, player);
					}
					
					temp = evaluationFuntion(isVisitedCopy, 3, !player);

					if (player && temp > value) {
						movei = i;
						movej = j;
						value = temp;
					} else if (!player && temp < value) {
						movei = i;
						movej = j;
						value = temp;
					}
				}
			}
		}
		
		isVisited[movei][movej] = player;
		System.out.println(movei + " " + movej + " " + value);
		makeMoveAdditional(deepCopy(isVisited), depth + 1, !player);
	}
	
	private void run() throws IOException
	{
		board = new int[N_VAL][N_VAL];

		BufferedReader br = new BufferedReader(new FileReader("hw1/2.2/Westerplatte.txt"));
		String str;
		String[] strList;
		int count = 0;
		
		while ((str = br.readLine()) != null) {
			strList = str.split("\t");
			for (int i=0; i<strList.length; i++) {
				board[count][i] = Integer.parseInt(strList[i]);
			}
			count++;
		}
	
		Boolean[][] isVisited = new Boolean[N_VAL][N_VAL];
		for (int i=0; i<N_VAL; i++) {
			for (int j=0; j<N_VAL; j++) {
				isVisited[i][j] = null;
			}
		}
		
		makeMove(isVisited, 0, true);
		
		isVisited = new Boolean[N_VAL][N_VAL];
		for (int i=0; i<N_VAL; i++) {
			for (int j=0; j<N_VAL; j++) {
				isVisited[i][j] = null;
			}
		}
		
		makeMoveAdditional(isVisited, 0, true);
	}
	
	public static void main(String[] args) throws IOException
	{
		Game main = new Game();
		main.run();
		System.exit(0);
	}
}
		