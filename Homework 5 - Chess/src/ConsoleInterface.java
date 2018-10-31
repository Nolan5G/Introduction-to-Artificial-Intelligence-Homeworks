import java.util.Scanner;

public class ConsoleInterface {
	//====================
	//  PRE-PROCESSING ERROR CHECKING
	//====================
	public static final int ERROR_CODE_LENGTH = 1;
	
	public static void printInvalidArguments(int numArguements, int consoleInterfaceErrorCode) {
		System.out.println("================");
		System.out.println("ERROR");
		System.out.println("================");
		System.out.println("Invalid usage of program \'Chess\'.");
		if(consoleInterfaceErrorCode == ConsoleInterfaceErrorCode.ERROR_CODE_LENGTH) {
			System.out.println("Number of arguments supplied: " + numArguements);
			System.out.println("Number of arguments expected: 2");
			System.out.println();
		}
		if(consoleInterfaceErrorCode == ConsoleInterfaceErrorCode.ERROR_CODE_VALUE_LTZ) {
			System.out.println("One of your arguments were less than zero.");
			System.out.println("Next time, enter a number greater than or equal to zero.");
			System.out.println();
		}
		System.out.println("Usage: java Chess [arg0] [arg1]");
		System.out.println("[arg0] = An integer Look-Ahead Depth for White (0 if player is human)");
		System.out.println("[arg1] = An integer Look-Ahead Depth for Black (0 if player is human)");
		System.out.println("================");
	}
	
	//====================
	//  RUNTIME ERROR CHECKING
	//====================	
	public static void printInvalidStringStateLength() {
		System.out.println("Invalid move length supplied.");
		System.out.println("Valid move format is \"[a-h][1-8][a-h][1-8]\".");
		System.out.println("Please try again!");
	}
	public static void printInvalidStringState() {
		System.out.println("Invalid move supplied.");
		System.out.println("Valid move format is \"[a-h][1-8][a-h][1-8]\".");
		System.out.println("Please try again!");
	}
	public static void printInvalidStringStateMove() {
		System.out.println("Invalid move supplied.");
		System.out.println("Piece cannot do desired move.");
		System.out.println("Please try again!");
	}
	
	//====================
	//  USER INPUT
	//====================
	public static int processStringState(String input, ChessState state) {
		try {
			int a, b, c, d;
			
			if(input.length() == 1 && input.equalsIgnoreCase("q")) {
				return -2;
			}
			
			if(input.length() != 4) {
				printInvalidStringStateLength();
				return -1;
			}
			
			a = ChessState.convertColumnAlphaToColumnNumber(input.substring(0, 1)) - 1;
			b = Integer.valueOf(input.substring(1, 2)) - 1;
			c = ChessState.convertColumnAlphaToColumnNumber(input.substring(2, 3)) - 1;
			d = Integer.valueOf(input.substring(3, 4)) - 1;
			
			if(!state.isValidMove(a, b, c, d)) {
				printInvalidStringStateMove();
				return -1;
			}
			
			return (state.move(a, b, c, d)) ? 1 : 0;
		} catch(NumberFormatException e) {
			printInvalidStringState();
			return -1;
		}
	}
	
	public static String getPlayerMove() {
		System.out.println("Your move?");
		Scanner scan = new Scanner(System.in);
		return scan.next();
	}
}

class ConsoleInterfaceErrorCode {
	public static final int ERROR_CODE_LENGTH = 1;
	public static final int ERROR_CODE_CAST = 2;
	public static final int ERROR_CODE_VALUE_LTZ = 3; 
}
