import java.util.Scanner;

public class Main {
	
	// =======================================
	//	Switch this flag to true displays all the scores 
	// =======================================
	public static final boolean DEBUG_FLAG = false;
	
	// =======================================
	// Configuration to play test puzzles against the AI.  Some are impossible for the AI to win, but it encourages AI to prolong.
	// =======================================
	public static final boolean CUSTOM_MAP = false;
	public static final char[][] CUSTOM_MAP_CONFIGURATION = new char[][] { {'1', 'X', '3'}, {'4', '5', '6'}, {'O', 'O', 'X'} };
	
	public static Scanner keyScanner;

	public static void main(String[] args) {
		keyScanner = new Scanner(System.in);
		boolean isActive = true;
		while(isActive) {
			Dialog.displayMainMenu();
			int choice = Dialog.getMainMenuChoice(keyScanner);
			System.out.println("-------------------------------------------");
			System.out.println();
			if(choice == 1) {
				Dialog.displayResponse1();
				
				GameState newGame;
				if(CUSTOM_MAP) {
					newGame = new GameState(CUSTOM_MAP_CONFIGURATION, GameState.State.ACTIVE);
				}
				else {
					newGame = new GameState();
				}
				
				boolean isGameActive = true;
				while(isGameActive) {
					
					newGame.printBoard();
					int moveChoice = Dialog.getMoveChoice(keyScanner);
					newGame = newGame.createChild(moveChoice, GameState.HUMAN);
					newGame = MiniMax.runAlgorithm(newGame, DEBUG_FLAG);
					
					if(newGame.currentState != GameState.State.ACTIVE) {
						System.out.println();
						newGame.printBoard();
						Dialog.displayResult(newGame.currentState);
						
						isGameActive = false;
					}
				}
				System.out.println();
				System.out.println("-------------------------------------------");
				System.out.println();
			}
			else if(choice == 2) {
				Dialog.displayResponse2();
				System.out.println();
				System.out.println("-------------------------------------------");
				System.out.println();
			}
			else if(choice == 3) {
				Dialog.displayResponse3();
				System.out.println();
				System.out.println("-------------------------------------------");
				System.out.println();
			}
			else if(choice == 4) {
				Dialog.displayResponse4();
				isActive = false;
			}
		}
	}
}
