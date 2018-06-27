import java.applet.Applet;
import java.applet.AudioClip;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * ClientMain represents one player of battleship. It is responsible for 
 * allowing the player to go online or stay offline to play a game.
 * @author  mm****, cm****, wb****, gf****
 */
public class ClientMain {

	/**
	 * Main method that determines how player wants to play, and initializes
	 * proper set up to do so.
	 * 
	 * @param args
	 */
	public static void main ( String[] args ) throws InterruptedException {

		/* create instance of game board */
		BattleshipGUI board = new BattleshipGUI();
		
		Sound s = new Sound();
//		try { //adding sonar sound
//			s.sonarSound();
//		} catch ( LineUnavailableException | UnsupportedAudioFileException
//		    || IOException e1 ) {
//			e1.printStackTrace();
//		}

		/* until user has chosen on option or other */
		while (board.offline_ == false && board.online_==false) {
			System.out.print("");
		}
		
		if (board.offline_ == true) { /* set up offline play */
			
		} else { /* playing online */ 
			try {
				while (board.server_.equals("")) {
					System.out.print("");
				}
				
				String server = board.server_; /* name of server */
				int port = 1992; /* server port number */
				
				/* connect to server with server and port input values */
				Socket connect = new Socket(server,port);

				/* set up bufferedReader an printWriter between client and server */
				BufferedReader reader = new BufferedReader(new InputStreamReader(connect.getInputStream()));
				PrintWriter writer = new PrintWriter(connect.getOutputStream(), true);
				
				/* while not all ships have been placed */
				while (board.shipsPlaced() == false) {
					System.out.print("");
				}
				
				int [][] placements = board.getBoard(); /* copy of players board */
				String coordinates = ""; /* string to write board info out to */
				
				for (int x = 0; x < 10; x++) {
					for (int y = 0; y < 10; y++) {
						/* add value of given spot to coordinates string */
						coordinates = coordinates + placements[x][y] + ";";
					}
				}
				writer.println(coordinates);			

				int oppHits = 0; /* hits on opponent */
				int hitsOnMe = 0; /* hits on player */
				String oppMessages = "";
				
				/* while game not over */
				while (oppHits < 31 || hitsOnMe < 31) {
					/* read input from server */
					String command = reader.readLine();
					
					/* if your turn, make move */
					if (command.equals("turn")) {
						board.chat_.setText("Your Turn!" + "--------" + oppMessages);
						/* while player hasn't clicked */
						while (board.getClick() == false) {
							System.out.print("");
						}

						/* tell server of move */
						writeMove(board, writer);
						
						/* wait to hear back if legal/if hit */
						String hitOrMiss = reader.readLine();	
						String[] temp1 = readHitMiss(hitOrMiss);
						
						/* if attack was hit */
						if (temp1[0].equals("hit")) {
							int x1 = Integer.parseInt(temp1[1]);
							int y1 = Integer.parseInt(temp1[2]);
							board.setAttack(2,x1,y1);
							oppHits++;
						} else { /* else was a miss */
							int x1 = Integer.parseInt(temp1[1]);
							int y1 = Integer.parseInt(temp1[2]);
							board.setAttack(3,x1,y1);
						}
						
						board.chat_.setText(hitOrMiss);
						
					} else if (command.equals("wait")) { /* else not your turn */
						board.chat_.setText("Please Wait..."  + "--------" + oppMessages);
						/* string to read hit or miss message from server */
						String hitOrMiss = reader.readLine();	
						/* array of contents of hit or miss */
						String[] temp2 = readHitMiss(hitOrMiss);
						
						/* if hit on me */
						if (temp2[0].equals("hit")) {
							int x2 = Integer.parseInt(temp2[1]);
							int y2 = Integer.parseInt(temp2[2]);
							board.setObserve(2,x2,y2);
							hitsOnMe++;
						}
						else { //else a miss */
							int x2 = Integer.parseInt(temp2[1]);
							int y2 = Integer.parseInt(temp2[2]);
							board.setObserve(3, x2, y2);
						}
						board.chat_.setText(hitOrMiss);
						
					} else if (command.equals("send messages")) { /* else write any waiting messages */
						writeMessage(board, writer);
						board.setMessage("");

						oppMessages = reader.readLine();
						//board.chat_.setText(oppMessages);
					}
				}
				
				/* if player won */
				if (oppHits == 31) {
					board.chat_.setText("Congrats, you won the game!");
				} else if (hitsOnMe == 31) { /* else opponent won */
					board.chat_.setText("Sorry, you lost the game...");
				}
				
			} catch (NumberFormatException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * writeMove method is responsible for writing the coordinates 
	 * of a players attack to the server via the provided PrintWriter.
	 * 
	 * @param move
	 * 		Represents coordinates of attack
	 * @param writer
	 * 		Represents PrintWriter to be used
	 */
	public static void writeMove (BattleshipGUI board, PrintWriter writer) {
		String move = (board.clickX - 6)/40  + "," + (board.clickY - 8)/40;
		writer.println(move);
		board.setClick(false);
	}

	/**
	 * writeMessage method is responsible for writing messages from
	 * the message box to the server via the PrintWriter provided.
	 * 
	 * @param message
	 * 		Represents message to be sent
	 * @param writer
	 * 		Represents PrintWriter to be used
	 */
	public static void writeMessage (BattleshipGUI board, PrintWriter writer) {
		String message = board.getMessage();
		writer.println("MESSAGE:" + message);
	}
	
	/**
	 * Method takes in string that represents hit or miss info
	 * from server and splits it up into the success of hit, and 
	 * coordinates of attack.
	 * 
	 * @param hitMiss
	 * 	string with information about attack from server
	 * @return
	 * 	array of strings holding contents of hitMiss
	 */
	public static String[] readHitMiss (String hitMiss) {
		return hitMiss.split(",");
	}
}
