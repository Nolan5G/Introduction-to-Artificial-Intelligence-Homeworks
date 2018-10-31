public class Main {
	public static void main(String[] args) {
		// Initial Configuration and Error Checking
		int lookAheadDepthWhite, lookAheadDepthBlack;
		try {
			if(args.length != 2) {
				ConsoleInterface.printInvalidArguments(args.length, ConsoleInterfaceErrorCode.ERROR_CODE_LENGTH);
				return;
			}
			
			lookAheadDepthWhite = Integer.valueOf(args[0]);
			lookAheadDepthBlack = Integer.valueOf(args[1]);
			
			if(lookAheadDepthWhite < 0 || lookAheadDepthBlack < 0) {
				ConsoleInterface.printInvalidArguments(args.length, ConsoleInterfaceErrorCode.ERROR_CODE_VALUE_LTZ);
			}
		}
		catch(NumberFormatException e) {
			ConsoleInterface.printInvalidArguments(args.length, ConsoleInterfaceErrorCode.ERROR_CODE_CAST);
			return;
		}
		
		// Usage Code
		ChessState game = new ChessState();
		ChessState.ChessMove m = null;
		
		game.resetBoard();
		
		int moveNumber = 1;
		boolean isWhiteMove = true;
		boolean isGameOver = false;
		boolean isWhiteWinner = false;
		while(!isGameOver) {
			if(isWhiteMove) {
				System.out.println("Move #" + moveNumber + " - White To Move:");
				game.printBoard(System.out);
				if(lookAheadDepthWhite > 0) {
					m = Agent.runMinimax(game, lookAheadDepthWhite, true).move;
					
					// Be advised.  NullPointerException is on the loose! He is described by police as a 6'4" Bulgarian man carrying a shiv.
					isGameOver = game.move(m.xSource, m.ySource, m.xDest, m.yDest);
				}
				else {
					int humanMoveResult = -1;
					while(humanMoveResult == -1) {
						humanMoveResult = ConsoleInterface.processStringState(ConsoleInterface.getPlayerMove(), game);
					}
					if(humanMoveResult == 1) {
						isGameOver = true;
						
					}
					else if(humanMoveResult == -2) {
						return;
					}
					else {
						isGameOver = false;
					}
				}
				
				if(isGameOver) {
					isWhiteWinner = true;
				}
				isWhiteMove = false;
			}
			else {
				System.out.println("Move #" + moveNumber + " - Black To Move:");
				game.printBoard(System.out);
				if(lookAheadDepthBlack > 0) {
					m = Agent.runMinimax(game, lookAheadDepthBlack, false).move;
					isGameOver = game.move(m.xSource, m.ySource, m.xDest, m.yDest);
				}
				else {
					int humanMoveResult = -1;
					while(humanMoveResult == -1) {
						humanMoveResult = ConsoleInterface.processStringState(ConsoleInterface.getPlayerMove(), game);
					}
					if(humanMoveResult == 1) {
						isGameOver = true;
					}
					else if(humanMoveResult == -2) {
						return;
					}
					else {
						isGameOver = false;
					}
				}
				isWhiteMove = true;
				moveNumber++;
			}
			System.out.println();
		}
		
		if(isWhiteWinner) {
			System.out.println("White is the winner");
		}
		else {
			System.out.println("Black is the winner");
		}
	}
}
