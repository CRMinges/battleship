/**
 * stores both player's game boards. GameLogic is used to see if a click by a 
 * player is a hit or miss; this information is given by GameLogic and used 
 * elsewhere in the program.
 * 
 * @author mm****, cm****, wb****, gf****
 */
public class GameLogic {

	int[][] p1Board;
	int[][] p2Board;

	public GameLogic (int[][] p1, int[][] p2) {
		p1Board = p1;
		p2Board = p2;
	}

	public boolean checkHit(boolean checkP2Board, int x, int y) {
		if (checkP2Board == true) {
			if (p2Board[x][y] == 1) {
				return true;
			} 
		} else {
			if (p1Board[x][y] == 1) {
				return true;
			} 
		}
		
		return false;
	}
}
