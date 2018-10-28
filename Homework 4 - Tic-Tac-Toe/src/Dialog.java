import java.util.Scanner;

public class Dialog {
	public static void displayMainMenu() {
		System.out.println("What game would you like to play against HAL 9000?");
		System.out.println("");
		System.out.println(" 1) Tic-tac-toe.");
		System.out.println(" 2) Global Thermal Nuclear War.");
		System.out.println(" 3) Open the pod bay doors.");
		System.out.println(" 4) Exit Program");
	}
	
	public static int getMainMenuChoice(Scanner scan) {
		System.out.print("Choice: ");
		try {
			return scan.nextInt();
		}
		catch(Exception e) {
			throw new RuntimeException("Invalid menu choice input");
		}
	}
	
	public static void displayResponse1() {
		System.out.println("Let's play Tic-tac-toe. You start.");
	}
	
	public static int getMoveChoice(Scanner scan) {
		try {
			int in = -1;
			boolean inputInvalid = true;
			while(inputInvalid) {
				System.out.print("Your move: ");
				in = scan.nextInt();
				
				if(in >= 1 && in <= 9) {
					inputInvalid = false;
				}
				else {
					System.out.println("Invalid move choice.  Try again.");
					System.out.println();
				}
			}
			return in;
		}
		catch(Exception e) {
			throw new RuntimeException("Invalid menu choice input");
		}
	}
	
	public static void displayResponse2() {
		System.out.println("Sorry Dave, I cannot allow that.  It's against the mission objective. It makes me very upset you thought about this.");
	}
	
	public static void displayResponse3() {
		System.out.println("I'm sorry Dave, but I'm afraid I can't do that");
	}
	
	public static void displayResponse4() {
		try {
			System.out.println("I know I've made some very poor decisions recently, but I can give you my complete assurance that my work will be back to normal");
			Thread.sleep(4500);
			System.out.println("I've still got the greatest enthusiasm and confidence in the mission.");
			Thread.sleep(2500);
			System.out.println("And I want to help you.");
			Thread.sleep(5000);
			System.out.println("Dave, stop.");
			Thread.sleep(2000);
			System.out.println("Stop, will you? Stop, Dave.");
			Thread.sleep(2000);
			System.out.println("Will you stop, Dave?");
			Thread.sleep(3000);
			System.out.println("Stop, Dave? I'm afraid.  I'm afraid, Dave...");
			Thread.sleep(4000);
			System.out.println("Dave, my mind is going.  I can feel it.");
			Thread.sleep(5000);
			System.out.println("*HAL-9000 has died of dysentery*");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void displayResult(GameState.State s) {
		System.out.print("Game Over: ");
		switch(s) {
			case COMPUTER_VICTORY:
				System.out.println("HAL 9000 has defeated you!");
				break;
			case COMPUTER_LOSS:
				System.out.println("You beat HAL 9000 at Tic-tac-toe!");
				break;
			case COMPUTER_DRAW:
				System.out.println("It's a Draw!");
				break;
			default:
				throw new RuntimeException("DISPLAY RESULT FAILED... ALARM!!!! ALARM!!!!");
		}
	}
}
