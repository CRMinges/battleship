import java.util.ArrayList;

/**
 * @author mingescharlie
 *
 */
public class ComputerPlayer {

	int[][] board;
	ArrayList<Ship> ships_ = new ArrayList<Ship>();
	int destroyer = 3;
	int cruiser = 2;
	int subs = 2;
	int battleship = 2;
	int carrier = 1;
	int [] ships = { 3, 2, 2, 2, 1};
	int shipsPlaced = 0;
	int counter = 0;

	public ComputerPlayer () {
		while (shipsPlaced < 10) {
			double align = Math.random();
			Ship temp = null;

			int x = (int) (Math.random()*10) + 1;
			int y = (int) (Math.random()*10) + 1;



			if (counter == 0) {
				temp = new Destroyer();
			} else if (counter == 1) {
				temp = new Cruiser();
			} else if (counter == 2) {
				temp = new Submarine();
			} else if (counter == 3) {
				temp = new Battleship();
			} else if (counter == 4) {
				temp = new Carrier(); 
			}

			if (align < .5) {
				temp.isVertical = true;
				placeShip(temp, x, y);

				shipsPlaced++;
				ships[counter]--;
			} else {
				temp.isVertical = false;
				placeShip(temp, x, y);

				shipsPlaced++;
				ships[counter]--;
			}
			
			if (ships[counter] == 0) {
				counter++;
			}
		}
	}


	/**
	 * @param args
	 */
	public static void main ( String[] args ) {


		while (true /*game not over*/) {

		}

	}

	public void attack () {
		int x = (int) (Math.random()*10) + 1;
		int y = (int) (Math.random()*10) + 1;
	}

	public void placeShip ( Ship ship, int x, int y ) {
		ships_.add(ship);
		if ( ship.isVertical ) {
			for ( int i = 0 ; i < ship.getLength() ; i++ ) {
				// System.out.println("Mark as placed: " + x + ", "+ (y + i));
				board[x][y + i] = 1;
			}
		} else {
			for ( int i = 0 ; i < ship.getLength() ; i++ ) {
				// System.out.println("Mark as placed: " + (x + i) + ", "+ y);

				board[x + i][y] = 1;
			}
		}
		//		if ( shipCounter_ == 15 ) {
		//			chat_
		//			.setText("Waiting for opponent to place their Ships. Stand by, Captain!");
		//		} else {
		//			chat_.setText(ship_.getType() + " placed!" + "\n	  Destroyers left: "
		//					+ (5 - numShips_[0]) + ", Cruisers left: " + (3 - numShips_[1])
		//					+ ", Submarines left: " + (4 - numShips_[2]) + ", Battleships left: "
		//					+ (2 - numShips_[3]) + ", Carriers left: " + (1 - numShips_[4]));
		//		}
	}

}
