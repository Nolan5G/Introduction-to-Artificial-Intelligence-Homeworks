import java.util.ArrayList;
import java.util.Comparator;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Color;

class Agent {
	
	private enum PollResult {
		NO_ACTION,
		UNIFORM_COST_SEARCH,
		A_STAR_SEARCH
	}
	
	private float goalX;
	private float goalY;
	private PollResult pollResult = PollResult.NO_ACTION;
	private Search s = new Search();

	void drawPlan(Graphics g, Model m) {
		g.setColor(Color.red);
		
		if(s.path != null && !s.path.isEmpty()) {
			float startX = m.getX(), startY = m.getY();
			
			State cs;
			while(!s.path.isEmpty()) {
				cs = s.path.pop();
				g.drawLine((int)startX, (int)startY, (int)cs.x, (int)cs.y);
				startX = cs.x;
				startY = cs.y;
			}
			
			g.setColor(Color.YELLOW);
			for(State d : s.frontier) {
				g.drawOval((int)d.x, (int)d.y, 10, 10);
			}
		}
	}

	void update(Model m)
	{
		Controller c = m.getController();
		
		while(true)
		{
			MouseEvent e = c.nextMouseEvent();
			if(e == null) {
				break;
			}
			if(e.getButton() == MouseEvent.BUTTON1) {
				pollResult = PollResult.UNIFORM_COST_SEARCH;
				goalX = e.getX();
				goalY = e.getY();
			}
			if(e.getButton() == MouseEvent.BUTTON3) {
				pollResult = PollResult.A_STAR_SEARCH;
				goalX = e.getX();
				goalY = e.getY();
			}
		}
		
		if(pollResult == PollResult.UNIFORM_COST_SEARCH) {
			if(m.getX() != goalX && m.getY() != goalY) {
				State result = s.uniform_cost_search(m, goalX, goalY);
				if(result != null) {
					m.setDestination(result.x, result.y);
				}	
			}
			else {
				pollResult = PollResult.NO_ACTION;
			}
		}
		else if(pollResult == PollResult.A_STAR_SEARCH) {
			if(m.getX() != goalX && m.getY() != goalY) {
				State result = s.a_star_search(m, goalX, goalY);
				if(result != null) {
					m.setDestination(result.x, result.y);
				}	
			}
			else {
				pollResult = PollResult.NO_ACTION;
			}
		}
	}

	public static void main(String[] args) throws Exception
	{
		Controller.playGame();
	}
}
