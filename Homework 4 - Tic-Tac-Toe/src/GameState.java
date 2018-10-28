import java.util.ArrayList;
import java.util.List;

public class GameState {
	// Members
	private char[][] board;
	public State currentState;
	public int utility;
	public int moveCreatingState;
	private final int BOARD_SIZE = 3;
	public static final char HUMAN = 'X';
	public static final char COMPUTER = 'O';
	
	public enum State {
		COMPUTER_VICTORY,
		COMPUTER_LOSS,
		COMPUTER_DRAW,
		ACTIVE
	}
	
	// Constructors
	public GameState() {
		board = new char[][] { 	{'1', '2', '3'},
								{'4', '5', '6'},
								{'7', '8', '9'}};
		currentState = State.ACTIVE;
	}
	
	public GameState(final char[][] boardCopy, State stateCopy) {
		if(!isBoardValidSize(boardCopy)) {
			throw new RuntimeException("Boardsize passed is invalid.");
		}
		copyBoard(boardCopy);
		this.currentState = stateCopy;
	}
	
	// Public External-Facing Methods
	public int calculateScore(int depth) {
		State result = isWinConditionMet();
		if(result == State.COMPUTER_VICTORY) {
			currentState = result;
			return 10 - depth;
		}
		else if(result == State.COMPUTER_LOSS) {
			currentState = result;
			return depth - 10;
		}
		currentState = State.COMPUTER_DRAW;
		return 0;
	}
	
	public List<Pair> getValidMoves(final char playerId) {
		if(isPlayerIdValid(playerId)) {
			List<Pair> moveList = new ArrayList<Pair>();
			
			for(int i = 0; i < BOARD_SIZE; i++) {
				for(int j = 0; j < BOARD_SIZE; j++) {
					if(board[i][j] != HUMAN && board[i][j] != COMPUTER) {
						moveList.add(new Pair(i,j));
					}
				}
			}
			
			if(moveList.size() > 0) {
				return moveList;
			}
			
			return null;
		}
		
		throw new RuntimeException("Invalid PlayerId Passed to GameState.getValidMoves()");
	}
	
	public GameState createChild(Pair pair, char playerId) {
		if(isPlayerIdValid(playerId)) {
			GameState child = new GameState(this.board, this.currentState);
			child.board[pair.a][pair.b] = playerId;
			child.moveCreatingState = getMoveFromPair(pair);
			return child;
		}
		throw new RuntimeException("Invalid GameState Created!");	
	}
	
	public GameState createChild(int move, char playerId) {
		Pair pair = getMoveIndexPair(move);
		if(isPlayerIdValid(playerId)) {
			GameState child = new GameState(this.board, this.currentState);
			child.board[pair.a][pair.b] = playerId;
			child.moveCreatingState = move;
			return child;
		}
		throw new RuntimeException("Invalid GameState Created!");	
	}
	
	public void printBoard() {
		System.out.println();
		for(int i = 0; i < BOARD_SIZE; i++) {
			String row = "";
			for(int j = 0; j < BOARD_SIZE; j++) {
				row += " ";
				row += board[i][j];
				row += " ";
				if(j < BOARD_SIZE - 1) {
					row += "|";
				}
			}
			System.out.println(row);
			if(i < BOARD_SIZE - 1) {
				System.out.println("---+---+---");
			}
		}
	}
	
	public State isWinConditionMet() {
		// Search for horizontal wins
		for(int row = 0; row < BOARD_SIZE; row++) {
			if((board[row][0] == COMPUTER) && (board[row][1] == COMPUTER) && (board[row][2] == COMPUTER))
				return State.COMPUTER_VICTORY;
			if((board[row][0] == HUMAN) && (board[row][1] == HUMAN) && (board[row][2] == HUMAN))
				return State.COMPUTER_LOSS;
		}
		
		// Search for vertical wins
		for(int col = 0; col < BOARD_SIZE; col++) {
			if((board[0][col] == COMPUTER) && (board[1][col] == COMPUTER) && (board[2][col] == COMPUTER))
				return State.COMPUTER_VICTORY;
			if((board[0][col] == HUMAN) && (board[1][col] == HUMAN) && (board[2][col] == HUMAN))
				return State.COMPUTER_LOSS;
		}
			
		// Search diagonal for wins
		if ((board[0][0] == COMPUTER) && (board[1][1] == COMPUTER) && (board[2][2] == COMPUTER))
	            return State.COMPUTER_VICTORY;
	    if ((board[2][0] == COMPUTER) && (board[1][1] == COMPUTER) && (board[0][2] == COMPUTER))
	            return State.COMPUTER_VICTORY;
	    if ((board[0][0] == HUMAN) && (board[1][1] == HUMAN) && (board[2][2] == HUMAN))
            return State.COMPUTER_LOSS;
	    if ((board[2][0] == HUMAN) && (board[1][1] == HUMAN) && (board[0][2] == HUMAN))
            return State.COMPUTER_LOSS;
			
		return State.ACTIVE;
	}
	
	// Private Utility Methods
	private boolean isBoardValidSize(final char[][] boardCopy) {
		if(boardCopy.length != BOARD_SIZE) {
			return false;
		}
		for(int i = 0; i < boardCopy.length; i++) {
			if(boardCopy[i].length != BOARD_SIZE) {
				return false;
			}
		}
		return true;
	}
	
	private void copyBoard(final char[][] boardCopy) {
		board = new char[3][3];
		for(int i = 0; i < boardCopy.length; i++) {
			for(int j = 0; j < boardCopy[i].length; j++) {
				board[i][j] = boardCopy[i][j];
			}
		}
	}
	
	private boolean isPlayerIdValid(final char playerId) {
		return (playerId == HUMAN || playerId == COMPUTER) ? (true) : false;
	}
	
	private int getMoveFromPair(Pair pair) {
		if(pair.a == 0 && pair.b == 0) {
			return 1;
		}
		else if(pair.a == 0 && pair.b == 1) {
			return 2;
		}
		else if(pair.a == 0 && pair.b == 2) {
			return 3;
		}
		else if(pair.a == 1 && pair.b == 0) {
			return 4;
		}
		else if(pair.a == 1 && pair.b == 1) {
			return 5;
		}
		else if(pair.a == 1 && pair.b == 2) {
			return 6;
		}
		else if(pair.a == 2 && pair.b == 0) {
			return 7;
		}
		else if(pair.a == 2 && pair.b == 1) {
			return 8;
		}
		else if(pair.a == 2 && pair.b == 2) {
			return 9;
		}
		else {
			throw new RuntimeException("Cannot convert pair to move number error! in getMoveFromPair()");
		}
	}
	
	private Pair getMoveIndexPair(int move) {
		Pair p = new Pair();
		switch(move) {
			case 1:
				p.a = 0;
				p.b = 0;
				break;
			case 2:
				p.a = 0;
				p.b = 1;
				break;
			case 3:
				p.a = 0;
				p.b = 2;
				break;
			case 4:
				p.a = 1;
				p.b = 0;
				break;
			case 5:
				p.a = 1;
				p.b = 1;
				break;
			case 6:
				p.a = 1;
				p.b = 2;
				break;
			case 7:
				p.a = 2;
				p.b = 0;
				break;
			case 8:
				p.a = 2;
				p.b = 1;
				break;
			case 9:
				p.a = 2;
				p.b = 2;
				break;
			default:
				return null;
		}
		return p;
	}
}
