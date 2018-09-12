import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.TreeSet;

public class Search {
	public Stack<State> path;
	public PriorityQueue<State> frontier;
	TreeSet<State> visited;
	State start;
	State goal;
	
	private void initSearch() {
		if(path != null) { path = null; }
		visited = new TreeSet<State>(new StateComparator());
	}
	
	public State uniform_cost_search(Model model, float goalX, float goalY) {
		frontier = new PriorityQueue<State>(new CostComparator());
		initSearch();
		start = new State(0.0, null, ((int)(model.getX() / 10))*10, ((int)(model.getY() / 10))*10);
		goal = new State(0.0, null, ((int)(goalX / 10))*10, ((int)(goalY / 10))*10);
		

		visited.add(start);
		frontier.add(start);
		
		while(frontier.size() > 0) {
			State currentState = frontier.poll();
			
			// Goal Check
			if(isGoalReached(currentState, goal)) {
				buildPath(currentState);
				return getNextStepState();
			}
			
			// Visit all the Child Nodes
			for(int i = 0; i < 9; i++) {
				if(i == 4) { continue; } // Ignore the child that does not move.
				
				State child = currentState.createChild(i);
				if(child != null) {
					float costOfMove = currentState.calculateCost(i, model.getTravelSpeed(currentState.x, currentState.y));
					
					if(visited.contains(child)) {
						State oldChild = visited.floor(child);
						if(currentState.cost + costOfMove < oldChild.cost) {
							oldChild.cost = currentState.cost + costOfMove;
							oldChild.parent = currentState;
						}
					}
					else {
						child.cost = currentState.cost + costOfMove;
						child.parent = currentState;
						frontier.add(child);
						visited.add(child);
					}
				}	
			}
		}
		System.out.println("There is no path to the goal!");
		return null;
	}

	public State a_star_search(Model model, float goalX, float goalY) {
		frontier = new PriorityQueue<State>(new AStarCostComparator());
		initSearch();
		start = new State(0.0, null, ((int)(model.getX() / 10))*10, ((int)(model.getY() / 10))*10);
		goal = new State(0.0, null, ((int)(goalX / 10))*10, ((int)(goalY / 10))*10);
		

		visited.add(start);
		frontier.add(start);
		
		while(frontier.size() > 0) {
			State currentState = frontier.poll();
			
			// Goal Check
			if(isGoalReached(currentState, goal)) {
				buildPath(currentState);
				return getNextStepState();
			}
			
			// Compute the next state
			for(int i = 0; i < 9; i++) {
				if(i == 4) { continue; } // Don't count the stalling state
				
				State child = currentState.createChild(i);
				if(child != null) {
					float costOfMove = currentState.calculateCost(i, model.getTravelSpeed(currentState.x, currentState.y));
					
					if(visited.contains(child)) {
						State oldChild = visited.floor(child);
						if(currentState.cost + costOfMove < oldChild.cost) {
							oldChild.cost = currentState.cost + costOfMove;
							oldChild.heuristic = a_star_heuristic(currentState, goal);
							oldChild.parent = currentState;
						}
					}
					else {
						// Unexplored Node Discovered! 
						child.cost = currentState.cost + costOfMove;
						child.heuristic = a_star_heuristic(currentState, goal);
						child.parent = currentState;
						frontier.add(child);
						visited.add(child);
					}
				}	
			}
		}
		System.out.println("There is no path to the goal!");
		return null;
	}

	public static float a_star_heuristic(State a, State b) {
		float euclidean_cost = Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
		return euclidean_cost * 0.2f;
	}
	
	private boolean isGoalReached(State currentState, State goalState) {
		return (Math.abs(goal.x - currentState.x) <= 10 && Math.abs(goal.y - currentState.y) <= 10 );
	}
	
	private void buildPath(State currentAndGoalState) {
		
		State traverse;
		State result = null;
		path = new Stack<State>();
		
		traverse = currentAndGoalState;
		while(traverse != null) {
			path.addElement(traverse);
			traverse = traverse.parent;
		}
		
	}
	
	private State getNextStepState() {
		State result = null;
		if(path != null) {
			State removedItem = path.pop();
			if(!path.isEmpty()) {
				result = path.peek();
			}
			path.push(removedItem);			
		}
		return result;
	}
}

class State {
	public double cost;
	public State parent;
	public float x, y;
	public float heuristic;
	
	public static final int MOVE_DISTANCE = 10;
	public static final float DIAGONAL_DISTANCE = (float)Math.sqrt(2*MOVE_DISTANCE*MOVE_DISTANCE);
	
	State() {
		this.cost = 0;
		this.parent = null;
		this.x = 0;
		this.y = 0;
		this.heuristic = 0;
	}
	
	State(double cost, State parent, float x, float y) {
		this.cost = cost;
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.heuristic = 0;
	}
	
	public boolean isStateEqualTo(State otherState) {
		return ((int)this.x == (int)otherState.x && (int)this.y == (int)otherState.y);
	}
	
	// Creates a child based off a 3x3 
	public State createChild(int transitionId) {
		State childState = new State();
		switch(transitionId) {
			case 0:
				childState.x = this.x - MOVE_DISTANCE; 
				childState.y = this.y - MOVE_DISTANCE;
				break;
			case 1:
				childState.x = this.x;
				childState.y = this.y - MOVE_DISTANCE;
				break;
			case 2:
				childState.x = this.x + MOVE_DISTANCE; 
				childState.y = this.y - MOVE_DISTANCE;
				break;
			case 3:
				childState.x = this.x - MOVE_DISTANCE; 
				childState.y = this.y;
				break;
			case 5:
				childState.x = this.x + MOVE_DISTANCE; 
				childState.y = this.y;
				break;
			case 6:
				childState.x = this.x - MOVE_DISTANCE; 
				childState.y = this.y + MOVE_DISTANCE;
				break;
			case 7:
				childState.x = this.x;
				childState.y = this.y + MOVE_DISTANCE;
				break;
			case 8:
				childState.x = this.x + MOVE_DISTANCE; 
				childState.y = this.y + MOVE_DISTANCE;
				break;
			default:
				System.out.println("ERROR: INVALID TRANSITION ID =============================================");
				childState = null;
		}
		if(childState.x < 0 || childState.x >= Model.XMAX || childState.y < 0 || childState.y >= Model.YMAX) {
			childState = null;
		}
		
		return childState;
	}

	public float calculateCost(int transitionId, float travelSpeed) {
		if(transitionId == 0 || transitionId == 2 || transitionId == 6 || transitionId == 8)
				return (DIAGONAL_DISTANCE / travelSpeed); 
		else if(transitionId == 1 || transitionId == 3 || transitionId == 5 || transitionId == 7)
				return (MOVE_DISTANCE / travelSpeed);
		else {
				System.out.println("ERROR: INVALID TRANSITION ID =============================================");
				return -1;
		}
	}
}

class StateComparator implements Comparator<State>
{
	public int compare(State a, State b)
	{
		// Compares the states based on discrete x,y, coordinates
		int result = Float.compare(a.x, b.x);
		if(result == 0) {
			result = Float.compare(a.y, b.y);
		}
		return result;
	}
}

class CostComparator implements Comparator<State>
{
	public int compare(State a, State b)
	{
		// Compares the states based on each state's cost values
		if(a.cost < b.cost) {
			return -1;
		}
		else if(a.cost > b.cost) {
			return 1;
		}
		return 0;
	}
}

class AStarCostComparator implements Comparator<State>
{
	public int compare(State a, State b)
	{		
		// Compares the states based on each state's cost values
		if((a.cost + a.heuristic) < (b.cost + b.heuristic)) {
			return -1;
		}
		else if((a.cost + a.heuristic) > (b.cost + b.heuristic)) {
			return 1;
		}
		return 0;
	}
}
