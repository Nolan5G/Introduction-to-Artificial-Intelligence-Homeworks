import java.util.List;

public class MiniMax {
	
	public static GameState originNode;
	public static boolean debugFlag;
	
	public static GameState runAlgorithm(GameState root, boolean flag) {
		originNode = root;
		debugFlag = flag;
		return runAlgorithm(root, 0, true);
	}
	
	public static GameState runAlgorithm(GameState node, int depth, final boolean maximizingPlayer) {
		List<Pair> validMoves = node.getValidMoves((maximizingPlayer) ? GameState.COMPUTER : GameState.HUMAN);
		
		if(validMoves == null || node.isWinConditionMet() != GameState.State.ACTIVE) {
			node.utility = node.calculateScore(depth);
			return node;
		}
		
		if(maximizingPlayer) {
			if(node.equals(originNode) && debugFlag) {
				System.out.print("Candidate Utility =");
			}
			
			GameState max = new GameState();
			max.utility = Integer.MIN_VALUE;
			for(Pair move : validMoves) {
				GameState child = node.createChild(move, GameState.COMPUTER);
				child.utility = runAlgorithm(child, depth + 1, false).utility;
				if(node.equals(originNode) && debugFlag) {
					System.out.print(child.utility + " ");
				}
				max = max(max, child);
			}
			if(node.equals(originNode) && debugFlag) {
				System.out.println("");
			}
			
			return max;
		}
		else {
			GameState min = new GameState();
			min.utility = Integer.MAX_VALUE;
			for(Pair move : validMoves) {
				GameState child = node.createChild(move, GameState.HUMAN);
				child.utility = runAlgorithm(child, depth + 1, true).utility;
				min = min(min, child);
			}
			return min;
		}
	}
	
	private static GameState max(GameState a, GameState b) {
		if(b.utility > a.utility) {
			return b;
		}
		else {
			return a;
		}
	}
	
	private static GameState min(GameState a, GameState b) {
		if(b.utility > a.utility) {
			return a;
		}
		else {
			return b;
		}
	}
}
