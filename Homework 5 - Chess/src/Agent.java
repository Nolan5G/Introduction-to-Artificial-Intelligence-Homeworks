public class Agent {
	public static final boolean WHITE_TO_MOVE = true;
	public static final boolean BLACK_TO_MOVE = false;
	
	@SuppressWarnings("unused")
	private static final boolean DEBUG_FLAG = true;
	
	public static class MinimaxResult {
		ChessState.ChessMove move;
		int utility;
	}
	
	
	public static MinimaxResult runMinimax(ChessState root, int searchDepth, boolean isMaximizingPlayer) {
		return runMinimax(root, searchDepth, -100000, 100000, isMaximizingPlayer, false);
	}
	
	// Returns the max / min state that has an updated heuristic and has info on the move that caused the state to arrive.
	private static MinimaxResult runMinimax(ChessState gameState, int depth, int alpha, int beta, boolean isMaximizingPlayer, boolean isGameOver) {
		if(depth == 0 || isGameOver) {
			MinimaxResult finalResult = new MinimaxResult();
			finalResult.move = null;
			finalResult.utility = gameState.heuristic();
			return finalResult;
		}
		
		MinimaxResult result = new MinimaxResult();
		ChessState.ChessMoveIterator itr = gameState.iterator(isMaximizingPlayer);
		ChessState.ChessMove childMove;
		
		if(isMaximizingPlayer) {
			int maxUtility = -100000;
			
			while(itr.hasNext()) {
				childMove = itr.next();
				
				ChessState child = new ChessState(gameState);
				boolean moveResult = child.move(childMove);
				MinimaxResult childResult = runMinimax(child, depth - 1, alpha, beta, false, moveResult);
				maxUtility = Math.max(maxUtility, childResult.utility);
				
				if(maxUtility > alpha) {
					alpha = maxUtility;
					result.move = childMove;
				}
				
				if(alpha >= beta) {
					result.utility = maxUtility;
					return result;
				}
			}
			result.utility = maxUtility;
			return result;
		}
		else {
			int minUtility = 100000;
			
			while(itr.hasNext()) {
				childMove = itr.next();
				
				ChessState child = new ChessState(gameState);
				boolean moveResult = child.move(childMove);
				MinimaxResult childResult = runMinimax(child, depth - 1, alpha, beta, true, moveResult);
				minUtility = Math.min(minUtility, childResult.utility);
				
				if(minUtility < beta) {
					beta = minUtility;
					result.move = childMove;
				}
				
				if(alpha >= beta) {
					result.utility = minUtility;
					return result;
				}
			}
			result.utility = minUtility;
			return result;
		}
	}
}
