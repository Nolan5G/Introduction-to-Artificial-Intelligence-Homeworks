import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeSet;

public class Main {
	public static void main(String[] args) throws Exception {
		// Display Technical Solution
		Solution s = new Solution();
		s.run();
	}
}

class Solution {
	// Member Variables
	public final int piece_count = 11;
	public final byte[][] init_board = { 
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 0, 0, 0, 0, 1, 1, 1},
			{1, 1, 0, 0, 0, 0, 0, 0, 1, 1},
			{1, 0, 0, 0, 1, 0, 0, 0, 0, 1},
			{1, 0, 0, 1, 1, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 1, 0, 0, 0, 0, 0, 0, 1, 1},
			{1, 1, 1, 0, 0, 0, 0, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
		};
	public final byte[][] init_pieces = {
			{1, 3, 2, 3, 1, 4, 2, 4},
			{1, 5, 1, 6, 2, 6},
			{2, 5, 3, 5, 3, 6},
			{3, 7, 3, 8, 4, 8},
			{4, 7, 5, 7, 5, 8},
			{6, 7, 6, 8, 7, 7},
			{5, 4, 4, 5, 5, 5, 5, 6},
			{6, 4, 6, 5, 6, 6, 7, 5},
			{8, 5, 8, 6, 7, 6},
			{6, 2, 6, 3, 5, 3},
			{5, 1, 5, 2, 6, 1}
	};
	public byte[][] board;
	private GameState state;
	
	// Constructors
	public Solution() {
		state = new GameState();
		board = new byte[10][10];
	}
	
	// Methods
	public void run() {
		placeInitPieces();
		//printBoard();
		breadth_first_search(state);
	}
	
	private void printBoard() {
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				System.out.print(board[i][j]);
			}
			System.out.println("");
		}
	}
	
	private void placeInitPieces() {
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				board[i][j] = init_board[i][j];
			}
		}
		
		for(int i = 0; i < piece_count; i++) {
			for(int j = 0; j < init_pieces[i].length; j += 2) {
				int x = (int)init_pieces[i][j];
				int y = (int)init_pieces[i][j+1];
				board[y][x] = (byte)(i + 2);
			}
		}
	}
	
	private void constructBoardFromState(final GameState s) {
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				board[i][j] = init_board[i][j];
			}
		}
		
		for(int i = 0; i < piece_count; i++) {
			for(int j = 0; j < init_pieces[i].length; j += 2) {
				int x = s.coords[i * 2] + init_pieces[i][j];
				int y = s.coords[(i * 2) + 1] + init_pieces[i][j+1];
				board[y][x] = (byte)(i + 2);
			}
		}
	}
	
	private void movePiece(int pieceIndex, int dX, int dY) {
		// Erase the piece
		for(int i = 0; i < init_pieces[pieceIndex].length; i += 2) {
			int x = (int)init_pieces[pieceIndex][i];
			int y = (int)init_pieces[pieceIndex][i+1];
			board[y][x] = 0;
		}
		
		// Draw the piece
		for(int i = 0; i < init_pieces[pieceIndex].length; i += 2) {
			int x = (int)init_pieces[pieceIndex][i] + dX;
			int y = (int)init_pieces[pieceIndex][i+1] + dY;
			board[y][x] = (byte)(pieceIndex + 2);
		}
	}
	
	private void breadth_first_search(GameState root) {
		StateComparator comp = new StateComparator();
		TreeSet<GameState> set_visited = new TreeSet<GameState>(comp);
		Queue<GameState> queue_todo = new LinkedList<GameState>();
		
		queue_todo.add(root);
		set_visited.add(root);
		
		while(!queue_todo.isEmpty()) {
			GameState n;
			
			n = queue_todo.remove();
			
			// Check if goal state is reached
			if((n.coords[1] + 3) == 1) {
				// Display the solution
				Stack<GameState> stack = new Stack<GameState>();
				GameState traverse = n;
				while(traverse != null) {
					stack.add(traverse);
					traverse = traverse.prev;
				}
				
				
				int length = stack.size();
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter("results.txt"));
					for(int i = 0; i < length; i++) {
						traverse = stack.pop();
						traverse.print();
						writer.write(traverse.toString());
						writer.newLine();
					}
					writer.close();
				}
				catch(IOException e) {
					e.printStackTrace();
				}

				break;
			}
			
			// Reconstruct the current board state
			GameState tempGameState = null;
			constructBoardFromState(n);
			
			for(int pieceIndex = 0; pieceIndex < piece_count; pieceIndex++) {
				// Left
				if(isValid(n, pieceIndex, -1, 0)) {
					tempGameState = n.createDeepCopyFromChanges(pieceIndex, -1, 0);
					if(!set_visited.contains(tempGameState)) {
						queue_todo.add(tempGameState);
						set_visited.add(tempGameState);
					}
				}
				
				// Right
				if(isValid(n, pieceIndex, 1, 0)) {
					tempGameState = n.createDeepCopyFromChanges(pieceIndex, 1, 0);
					if(!set_visited.contains(tempGameState)) {
						queue_todo.add(tempGameState);
						set_visited.add(tempGameState);
					}
				}
				
				// Up
				if(isValid(n, pieceIndex, 0, -1)) {
					tempGameState = n.createDeepCopyFromChanges(pieceIndex, 0, -1);
					if(!set_visited.contains(tempGameState)) {
						queue_todo.add(tempGameState);
						set_visited.add(tempGameState);
					}
				}
				
				// Down
				if(isValid(n, pieceIndex, 0, 1)) {
					tempGameState = n.createDeepCopyFromChanges(pieceIndex, 0, 1);
					if(!set_visited.contains(tempGameState)) {
						queue_todo.add(tempGameState);
						set_visited.add(tempGameState);
					}
				}
			}
			
		}
	}
	
	private boolean isValid(GameState s, int pieceIndex, int dX, int dY) {
		// Erase piece from current state
		for(int i = 0; i < init_pieces[pieceIndex].length; i += 2) {
			int x = s.coords[pieceIndex * 2] + init_pieces[pieceIndex][i];
			int y = s.coords[(pieceIndex * 2) + 1] + init_pieces[pieceIndex][i+1];
			board[y][x] = 0;
		}
		
		// Draw piece at new location
		boolean validFlag = true;
		for(int i = 0; i < init_pieces[pieceIndex].length; i += 2) {
			int x = s.coords[pieceIndex * 2] + init_pieces[pieceIndex][i] + dX;
			int y = s.coords[(pieceIndex * 2) + 1] + init_pieces[pieceIndex][i+1] + dY;
			
			// If the write location is anything but 0, invalid state.
			if(board[y][x] != 0) {
				validFlag = false;
				break;
			}
		}
		
		// Redraw erased piece
		for(int i = 0; i < init_pieces[pieceIndex].length; i += 2) {
			int x = s.coords[pieceIndex * 2] + init_pieces[pieceIndex][i];
			int y = s.coords[(pieceIndex * 2) + 1] + init_pieces[pieceIndex][i+1];
			board[y][x] = (byte)(pieceIndex + 2);
		}
		
		return validFlag;
	}
}

class GameState {
	GameState prev;
	byte[] coords;
	
	public GameState(GameState prev)
	{
		this.prev = prev;
		coords = new byte[22];
	}
	
	public GameState()
	{
		this(null);
	}
	
	public GameState createDeepCopyFromChanges(int pieceIndex, int dX, int dY) {
		// Create and initialize new deep copy
		GameState newGameState = new GameState(this);
		for(int i = 0; i < 22; i++) {
			newGameState.coords[i] = this.coords[i];
		}
		
		// Commit changes to the new deep copy
		newGameState.coords[pieceIndex * 2] += dX;
		newGameState.coords[(pieceIndex * 2) + 1] += dY;
		
		return newGameState;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < coords.length - 2; i+=2) {
			//sb.append("(");
			sb.append(Byte.toString(coords[i]));
			sb.append(",");
			sb.append(Byte.toString(coords[i+1]));
			sb.append(",");
			//sb.append(") ");
		}
		sb.append(Byte.toString(coords[20]));
		sb.append(",");
		sb.append(Byte.toString(coords[21]));
		
		return sb.toString();
	}
	
	public void print() {
		System.out.println(this.toString());
	}
}

class StateComparator implements Comparator<GameState>
{

	@Override
	public int compare(GameState a, GameState b)
	{
		for(int i = 0; i < 22; i++)
		{
			if(a.coords[i] < b.coords[i])
				return -1;
			else if(a.coords[i] > b.coords[i])
				return 1;
		}
		return 0;
	}
	
}