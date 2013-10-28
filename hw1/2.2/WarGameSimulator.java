import java.io.BufferedReader;
import java.io.Console;
import java.io.FileReader;
import java.io.IOException;


/*
The above code simulates the war game. 
Conventions used -
player = +1 : MAX Player
player = -1 : MIN Player 
moveTupe = +1 : Commando Para Drop
moveTupe = -1 : M1 Death Blitz
*/

class Node {
	int value;
	int ownedBy;
	
	Node(int value, int owner){
		this.value = value;
		this.ownedBy = owner;
	}
}

class MoveInfo{
	
	int utility;
	Node[][] gameState;
	int moveType;
	int x;
	int y;
	
	MoveInfo(int u, Node[][] state, int m, int x, int y){
		utility = u;
		this.gameState = state;
		this.moveType = m;
		this.x = x;
		this.y = y;
	}
}

class WarGame{
	int SIZE = 6;
	int GAME_TREE_DEPTH = 3;
	Node[][] gameState = new Node[SIZE][SIZE];
	Node[][] stateAfterEachMove;
	
	int alpha = -99999;
	int beta =  +99999;
	int mainPlayer;
	
	
	private Node[][] doDeepCopy(Node[][] gameState){
		Node[][] tmp = new Node[SIZE][SIZE];
		
		for(int i = 0;i<SIZE;i++){
			for(int j = 0;j<SIZE;j++){
				tmp[i][j] = new Node(gameState[i][j].value, gameState[i][j].ownedBy);
			}
		}
		return tmp;
	}
	
	private boolean checkNeighbourCell(Node [][]state, int x, int y, int player){
		if(x-1>=0 && state[x-1][y].ownedBy == player) return false;
		if(x+1<SIZE && state[x+1][y].ownedBy == player) return false;
		if(y-1>=0 && state[x][y-1].ownedBy == player) return false;	
		if(y+1<SIZE && state[x][y+1].ownedBy == player) return false;
		else return true;
	}
	
	public void initializeState(String filename) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(filename)); 
		String line;
		int sz;
		int row = 0, column = 0;
		while ((line = reader.readLine()) != null) {
			String[] val = line.split("\t");
			column = 0;
			for(int j = 0;j<val.length;j++){
				gameState[row][column++] = new Node(Integer.parseInt(val[j]), 0);
			}
			row++;
		}
	}
	
	public MoveInfo playGame(Node[][] state, int player, int depth, int alpha, int beta){
		if(depth == 0) return null; 
		
		// for each possible move for the player build the game tree
		int maxUtility = -9999;
		int minUtility = +9999;
		int nextPlayer;
		MoveInfo move = null;
		nextPlayer = player > 0 ? -1 : 1; 
		for(int i = 0;i<SIZE;i++){
			for(int j = 0;j<SIZE;j++){
				
				if(state[i][j].ownedBy == 0 && checkNeighbourCell(state,i,j,player)){
					//System.out.println("Evaluating " + i + "," + j + " at depth " + depth);
					Node[][] tmp = doDeepCopy(state);
					tmp[i][j].ownedBy = player;
					MoveInfo m = playGame(doDeepCopy(tmp), nextPlayer, depth - 1, alpha, beta);
					//int u = m == null ? state[i][j].value : 0;
					int u =0;
					Node [][]x = m == null ? tmp:m.gameState; 
					for(int i1 = 0;i1<SIZE;i1++){
						for(int j1 = 0;j1<SIZE;j1++){
							if(x[i1][j1].ownedBy == +1){
								u = u + x[i1][j1].value;
							}else if(x[i1][j1].ownedBy == -1){
								u = u - x[i1][j1].value;
							}
						}
					}	
					if(player == 1 && u > maxUtility){
						maxUtility = u;
						move = new MoveInfo(maxUtility, tmp, 1, i,j);
					}
					if(player == -1 && u < minUtility){
						minUtility = u;
						move = new MoveInfo(minUtility, tmp, 1, i, j);
					}
				}
				
				// try to expand the territory 
				if(state[i][j].ownedBy == player){
					//System.out.println("Expanding territory !");
					
					// 1st. possible expansion move
					if(i >=0 && j-1 >=0 && state[i][j-1].ownedBy == 0){
						Node[][] tmp = doDeepCopy(state);
						int u = 0;
						tmp[i][j-1].ownedBy = player;
						if(i-1 >=0 && j-1 >=0 && tmp[i-1][j-1].ownedBy == nextPlayer){
							tmp[i-1][j-1].ownedBy = player;
							
						}
						if(i>=0 && j-2 >=0 && tmp[i][j-2].ownedBy == nextPlayer){
							tmp[i][j-2].ownedBy = player;
							
						}
						if(i+1<SIZE && j-1 >=0 && tmp[i+1][j-1].ownedBy == nextPlayer){
							tmp[i+1][j-1].ownedBy = player;
							
						}
						MoveInfo m = playGame(doDeepCopy(tmp), nextPlayer, depth - 1, alpha, beta);
						Node [][]x = m == null ? tmp:m.gameState;
						for(int i1 = 0;i1<SIZE;i1++){
							for(int j1 = 0;j1<SIZE;j1++){
								if(x[i1][j1].ownedBy == +1){
									u = u + x[i1][j1].value;
								}else if(x[i1][j1].ownedBy == -1){
									u = u - x[i1][j1].value;
								}
							}
						}
						if(player == +1 && u > maxUtility){
							maxUtility = u;
							move = new MoveInfo(maxUtility, tmp, 2, i, j-1);
						}
						if(player == -1 && u < minUtility){
							minUtility = u;
							move = new MoveInfo(minUtility, tmp, 2, i, j-1);
						}
						
					}
					// 2nd possible move.
					if(i >=0 && j+1 < SIZE && state[i][j+1].ownedBy == 0){
						
						//System.out.println("Empty found");
						Node[][] tmp = doDeepCopy(state);
						int u = 0;
						tmp[i][j+1].ownedBy = player;
						if(i-1 >=0 && j+1 <SIZE && tmp[i-1][j+1].ownedBy == nextPlayer){
							tmp[i-1][j+1].ownedBy = player;
						}
						if(i>=0 && j+2 < SIZE && tmp[i][j+2].ownedBy == nextPlayer){
							tmp[i][j+2].ownedBy = player;
						}
						if(i+1<SIZE && j+1 >=0 && tmp[i+1][j+1].ownedBy == nextPlayer){
							tmp[i+1][j+1].ownedBy = player;
						}
						MoveInfo m = playGame(doDeepCopy(tmp), nextPlayer, depth - 1, alpha, beta);
						Node [][]x = m==null ? tmp : m.gameState;
						for(int i1 = 0;i1<SIZE;i1++){
							for(int j1 = 0;j1<SIZE;j1++){
								if(x[i1][j1].ownedBy == +1){
									u = u + x[i1][j1].value;
								}else if(x[i1][j1].ownedBy == -1){
									u = u - x[i1][j1].value;
								}
							}
						}
						if(player == +1 && u > maxUtility){
							maxUtility = u;
							move = new MoveInfo(maxUtility, tmp, 2, i, j+1);
						}
						if(player == -1 && u < minUtility){
							minUtility = u;
							move = new MoveInfo(minUtility, tmp, 2, i, j+1);
						}
						//System.out.println(maxUtility);
						
					}
					// 3rd possible move.
					if(i-1 >=0 && j >= 0 && state[i-1][j].ownedBy == 0){
						Node[][] tmp = doDeepCopy(state);
						int u = 0;
						tmp[i-1][j].ownedBy = player;
						if(i-1 >=0 && j-1 >=0 && tmp[i-1][j-1].ownedBy == nextPlayer){
							tmp[i-1][j-1].ownedBy = player;
						}
						if(i-2>=0 && j < SIZE && tmp[i-2][j].ownedBy == nextPlayer){
							tmp[i-2][j].ownedBy = player;
						}
						if(i-1>=0 && j+1 < SIZE && tmp[i-1][j+1].ownedBy == nextPlayer){
							tmp[i-1][j+1].ownedBy = player;
						}
						MoveInfo m = playGame(doDeepCopy(tmp), nextPlayer, depth - 1, alpha, beta);
						Node [][]x = m == null ? tmp : m.gameState;
						for(int i1 = 0;i1<SIZE;i1++){
							for(int j1 = 0;j1<SIZE;j1++){
								if(x[i1][j1].ownedBy == +1){
									u = u + x[i1][j1].value;
								}else if(x[i1][j1].ownedBy == -1){
									u = u - x[i1][j1].value;
								}
							}
						}
						if(player == +1 && u > maxUtility){
							maxUtility = u;
							move = new MoveInfo(maxUtility, tmp, 2, i, j-1);
						}else if(player == -1 && u < minUtility){
							minUtility = u;
							move = new MoveInfo(minUtility, tmp, 2, i, j-1);
						}
							
					}
					
					// 4th possible move.
					if(i+1 < SIZE && j >= 0 && state[i+1][j].ownedBy == 0){
						Node[][] tmp = doDeepCopy(state);
						int u = 0;
						tmp[i+1][j].ownedBy = player;
						if(j-1 >=0 && tmp[i+1][j-1].ownedBy == nextPlayer){
							tmp[i+1][j-1].ownedBy = player;
						}
						if(i+2 < SIZE && tmp[i+2][j].ownedBy == nextPlayer){
							tmp[i+2][j].ownedBy = player;
						}
						if(j+1 < SIZE && tmp[i+1][j+1].ownedBy == nextPlayer){
							tmp[i+1][j+1].ownedBy = player;
						}
						MoveInfo m = playGame(doDeepCopy(tmp), nextPlayer, depth - 1, alpha, beta);
						Node [][]x = m==null ? tmp :m.gameState;
						for(int i1 = 0;i1<SIZE;i1++){
							for(int j1 = 0;j1<SIZE;j1++){
								if(x[i1][j1].ownedBy == +1){
									u = u + x[i1][j1].value;
								}else if(x[i1][j1].ownedBy == -1){
									u = u - x[i1][j1].value;
								}
							}
						}
						if(player == +1 && u > maxUtility ){
							maxUtility = u;
							move = new MoveInfo(maxUtility, tmp, 2, i+1, j);
						}
						else if(player == -1 && u < minUtility ){
							minUtility = u;
							move = new MoveInfo(minUtility, tmp, 2, i+1, j);
						}
					}	
				}
				if(player == +1 && alpha < maxUtility){
					alpha = maxUtility;
				}
				if(player == -1 && beta > minUtility){
					beta = minUtility;
				}
				if(player == -1 && minUtility < alpha){
					//System.out.println("alpha Pruning");
					return move;
				}
				if(player == +1 && maxUtility > beta){
					//System.out.println("beta Pruning");
					return move;
				}
			}
		}
		/*System.out.println("Returning - ");
	    Node [][]x = move.gameState;
		int utility = move.utility;
		System.out.println("Max utility = " + maxUtility);
		System.out.println("Min utility = " + minUtility);
		System.out.println("Best Move !!!");
		for(int i = 0;i<SIZE;i++){
			for(int j = 0;j<SIZE;j++){
				System.out.print(x[i][j].ownedBy + "," + x[i][j].value + " ");
			}
			System.out.println();
		}*/ 
		return move;
	}
	
	public void play(){
		
		for(int i = 0;i<SIZE;i++){
			for(int j = 0;j<SIZE;j++){
				System.out.print(gameState[i][j].ownedBy + "," + gameState[i][j].value + " ");
			}
			System.out.println();
		}
		
		MoveInfo move;
		int count = 0;
		do{ 
			int player = count % 2 == 0 ? 1 : -1;
			mainPlayer = player;	
			alpha = -99999;
			beta = +99999;
			move = playGame(doDeepCopy(gameState),player,GAME_TREE_DEPTH, alpha, beta);
			
			if(move!=null){
				Integer x = (Integer)(move.x + 1);
				String chance = player == 1 ? "blue: " : "green: ";
				Character y = new Character((char)(move.y + 65));
				StringBuffer s = new StringBuffer(y.toString() + x.toString());
				String m = move.moveType == 1 ? "Commando Para Drop " : "M1 Death Blitz ";
				m = chance + m + s;
				System.out.println(m);
			}
			gameState = move !=null ? move.gameState : gameState;
			count++;
			//System.out.println("Player - " + player + " move ");
			//Console console = System.console();
			//String input = console.readLine("Enter input:");
		}while(move !=null);
		
		int blueScore = 0;
		int greenScore = 0;
		for(int i = 0;i<SIZE;i++){
			for(int j = 0;j<SIZE;j++){
				String owner = gameState[i][j].ownedBy == 1 ? "B" : "G";
				System.out.print(owner + "," + gameState[i][j].value + " ");
				if(gameState[i][j].ownedBy == +1) blueScore += gameState[i][j].value;
				if(gameState[i][j].ownedBy == -1) greenScore += gameState[i][j].value;
			}
			System.out.println();
		}
		System.out.println("BLUE Score " + blueScore);
		System.out.println("GREEN Score " + greenScore);
		
		
		
	}
	
	
	
}

public class WarGameSimulator{
	
	public static void main(String []arg) throws IOException{
		WarGame g = new WarGame();
		g.initializeState("hw1/2.2/Narvik.txt");
		g.play();
	}
}


