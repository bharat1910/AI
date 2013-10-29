import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GameCombined
{
	int N_VAL = 6;
	int[][] board;
	int[] moves = {1, 0, -1};
	double nodesExpanded;
	double nodesExpandedPerMove;
	double timeTaken;
	double nodesExpandedBlue;
	double nodesExpandedGreen;
	Double FRACTION_GRADIENT = 1/(double)2;
	Double FRACTION = 1.0;
	
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
	
	private boolean duel(Boolean[][] isVisited, boolean player)
	{
		int playerSum = 0, playerCount = 0, oPlayerSum = 0, oPlayerCount = 0;
		for (int i=0; i<N_VAL; i++) {
			for (int j=0; j<N_VAL; j++) {
				if (isVisited[i][j] == null) {
					continue;
				}
				if (isVisited[i][j] == player) {
					playerSum += board[i][j];
					playerCount++;
				} else if (isVisited[i][j] == !player) {
					oPlayerSum += board[i][j];
					oPlayerCount++;
				}
			}
		}
		
		if ((playerSum/(double)playerCount) >= (oPlayerSum/(double)oPlayerCount)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean battle(Boolean[][] isVisited, boolean player, int ipos, int jpos)
	{
		int playerSum = 0, playerCount = 0, oPlayerSum = 0, oPlayerCount = 0;
		double unitCountPlayer, unitCountOtherPlayer;
		for (int i=0; i<N_VAL; i++) {
			for (int j=0; j<N_VAL; j++) {
				if (isVisited[i][j] == null) {
					continue;
				}
				if (isVisited[i][j] == player) {
					playerSum += board[i][j];
					playerCount++;
				} else if (isVisited[i][j] == !player) {
					oPlayerSum += board[i][j];
					oPlayerCount++;
				}
			}
		}
		
		unitCountPlayer = playerSum/(double)playerCount;
		unitCountOtherPlayer = oPlayerSum/(double)oPlayerCount;
		playerCount = 0; oPlayerCount = 0;
		
		for (int i : moves) {
			for (int j : moves) {
				if (Math.abs(i) != Math.abs(j) &&
					ipos + i >= 0 &&
					ipos + i < N_VAL &&
					jpos + j >= 0 &&
					jpos + j < N_VAL &&
					isVisited[ipos+i][jpos+j] != null ) {
					if (isVisited[ipos+i][jpos+j] == player) {
						playerCount++;
					} else if (isVisited[ipos+i][jpos+j] == !player) {
						oPlayerCount++;
					}
				}
			}
		}
		
		if ((unitCountPlayer * playerCount) >= (unitCountOtherPlayer * oPlayerCount)) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean attrition(Boolean[][] isVisited, boolean player, int ipos, int jpos)
	{
		int playerSum = 0, playerCount = 0, oPlayerSum = 0, oPlayerCount = 0;
		double unitCountPlayer, unitCountOtherPlayer;
		for (int i=0; i<N_VAL; i++) {
			for (int j=0; j<N_VAL; j++) {
				if (isVisited[i][j] == null) {
					continue;
				}
				if (isVisited[i][j] == player) {
					playerSum += board[i][j];
					playerCount++;
				} else if (isVisited[i][j] == !player) {
					oPlayerSum += board[i][j];
					oPlayerCount++;
				}
			}
		}
		
		unitCountPlayer = playerSum/(double)playerCount;
		unitCountOtherPlayer = oPlayerSum/(double)oPlayerCount;
		playerCount = 0; oPlayerCount = 0;
		
		for (int i : moves) {
			for (int j : moves) {
				if (Math.abs(i) != Math.abs(j) &&
					ipos + i >= 0 &&
					ipos + i < N_VAL &&
					jpos + j >= 0 &&
					jpos + j < N_VAL &&
					isVisited[ipos+i][jpos+j] != null ) {
					if (isVisited[ipos+i][jpos+j] == player) {
						playerCount++;
					} else if (isVisited[ipos+i][jpos+j] == !player) {
						oPlayerCount++;
					}
				}
			}
		}
		
		if ((unitCountPlayer * playerCount * FRACTION) >= (unitCountOtherPlayer * oPlayerCount * FRACTION)) {
			return true;
		} else {
			return false;
		}
	}
	
	private void turnAdjacent(Boolean[][] isVisited, int i, int j, boolean player)
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
					if (battle(isVisited, player, i+u, j+v)) {
						isVisited[i+u][j+v] = player;						
					} else {
						isVisited[i+u][j+v] = !player;
					}
				}
			}
		}
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
			//System.out.println(diff(isVisited));
			return diff(isVisited);
		}

		int value, temp;
		if (player) {
			value = Integer.MIN_VALUE;
		}
		else {
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

					nodesExpandedPerMove++;
					
					temp = evaluationFuntionAlphaBeta(isVisitedCopy, depth - 1, !player, max, min);
					
					if (player && temp > value) {
						value = temp;
					} else if (!player && temp < value) {
						value = temp;
					}
					
					if (!player && value < min) {
						min = value;
					} else if (player && value > max) {
						max = value;
					}
					
					if (player && value > min) {
						return value;
					} else if (!player && value < max) {
						return value;
					}
				}
			}
		}

		return value;
	}
	
	private void makeMove(Boolean[][] isVisited, int depth,
			boolean player)
	{
		FRACTION *= FRACTION_GRADIENT;
		
		long start = System.currentTimeMillis();
		nodesExpandedPerMove = 0;
		
		if (depth == N_VAL * N_VAL) {
			System.out.println();
			int blueScore = 0, greenScore = 0;
			for (int i=0; i<N_VAL; i++) {
				for (int j=0; j<N_VAL; j++) {
					System.out.print(board[i][j]);
					if (isVisited[i][j]) {
						blueScore += board[i][j];
						System.out.print(",B ");
					} else {
						greenScore += board[i][j];
						System.out.print(",G ");
					}
				}
				System.out.println();
			}
			System.out.println();
			System.out.println("Blue Score : " + blueScore);
			System.out.println("Green Score : " + greenScore);
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
					
					nodesExpandedPerMove++;
					
					if (player) {
						// Depth is the value being passed + 1 since one level
						// of evaluation is performed by this method itself
						
						temp = evaluationFuntionAlphaBeta(isVisitedCopy, 4, !player, value, Integer.MAX_VALUE);
					}
					else {
						temp = evaluationFuntionAlphaBeta(isVisitedCopy, 4, !player, Integer.MIN_VALUE, value);
					}

					if (player && temp > value) {
						movei = i;
						movej = j;
						value = temp;
					} else if (!player && temp < value) {
						movei = i;
						movej = j;
						value = temp;
					}
					//System.out.println(value + " " + movei + " " + movej);
				}
			}
		}
		
		isVisited[movei][movej] = player;
		boolean check = false;
		if (hasAdjacent(isVisited, movei, movej, player)) {
			check = true;
			turnAdjacent(isVisited, movei, movej, player);
		}
		
		if (player) {
			System.out.print("blue:");
		} else {
			System.out.print("green:");
		}
		
		if (check) {
			System.out.print(" M1 Death Blitz ");
		} else {
			System.out.print(" Commando Para Drop ");
		}
		
		System.out.println(((char)('A' + movej)) + "" + (movei + 1));
		
		long end = System.currentTimeMillis();
		timeTaken += (end - start);
		nodesExpanded += nodesExpandedPerMove;
		if (player) {
			nodesExpandedBlue += nodesExpandedPerMove;
		} else {
			nodesExpandedGreen += nodesExpandedPerMove;
		}
		
		makeMove(deepCopy(isVisited), depth + 1, !player);
	}
	
	private void run() throws IOException
	{
		board = new int[N_VAL][N_VAL];
		nodesExpanded = 0;
		timeTaken = 0;
		nodesExpandedBlue = 0;
		nodesExpandedGreen = 0;

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

		System.out.println("Nodes expanded by Blue : " + ((long) nodesExpandedBlue));
		System.out.println("Nodes expanded by Green : " + ((long) nodesExpandedGreen));
		System.out.println("Nodes expanded per move : " + nodesExpanded/(N_VAL * N_VAL));
		System.out.println("Time taken per move : " + timeTaken/(N_VAL * N_VAL));
	}
	
	public static void main(String[] args) throws IOException
	{
		GameCombined main = new GameCombined();
		main.run();
		System.exit(0);
	}
}
		