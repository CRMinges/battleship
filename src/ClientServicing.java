import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *ClientServicing is responsible for running a game of battleship
 *between two clients via a network.
 *
 * @author mm****, cm****, wb****, gf****
 */
public class ClientServicing implements Runnable {

	private Socket connect1_; /* socket for one player */
	private Socket connect2_; /* socket for second player */
	boolean classic_; /* whether playing classic or timed game mode */

	public ClientServicing (Socket connect1, Socket connect2) {
		super();
		connect1_ = connect1;
		connect2_ = connect2;
	}


	@Override
	public void run () {
		try {
			/* creates bufferedReader and printWriter for with first players connection */
			BufferedReader p1Reader =
					new BufferedReader(new InputStreamReader(connect1_.getInputStream()));
			PrintWriter p1Writer = new PrintWriter(connect1_.getOutputStream(), true);

			/* creates bufferedReader and printWriter for with second players connection */
			BufferedReader p2Reader =
					new BufferedReader(new InputStreamReader(connect2_.getInputStream()));
			PrintWriter p2Writer = new PrintWriter(connect2_.getOutputStream(), true);

			
			boolean ready = false; 		/* ships placed ready for next stage of game */
			String placement1 = ""; 	/* string of coordinates of p1 ships */
			String placement2 = ""; 	/* string of coordinates of p2 ships */
			
			/* while both players ships arent placed */
			while (ready == false) {
				placement1 = p1Reader.readLine(); /* read in p1 placements */
				placement2 = p2Reader.readLine(); /* read in p2 placements */
				
				/* if they dont send over information */
				if (placement1.equals("") && placement2.equals("")) {
					ready = false;
				} else { /* else ready = true, break out of loop */
					ready = true;
				}
			}
			
			int[][] p1Board = makeBoard(placement1); /* player 1 board */
			int[][] p2Board = makeBoard(placement2); /* player 2 board */
			
			//new game logic class
			GameLogic logic = new GameLogic(p1Board, p2Board);
			
			String p1Messages = "";/* represents p1 messages outgoing */
			String p2Messages = "";/* represents p2 messages outgoing */
			
			
			int p1hits = 0; /* number of p1 hits */
			int p2hits = 0; /* number of p2 hits */
			boolean gameOn = true; /* if game is over or not */
			
			/* while game not over  */
			while (true) {
				
				/*-----------------player 1 turn------------------ */
				
				p1Writer.println("turn");
				p2Writer.println("wait");

				/* player1 attack */
				String p1Attack = p1Reader.readLine();
				String[] p1Coor = p1Attack.split(",");
				int p1x = Integer.parseInt(p1Coor[0]);
				int p1y = Integer.parseInt(p1Coor[1]);
			
				/* check if hit */
				if (logic.checkHit(true,p1x,p1y) == true) {
					p1Writer.println("hit" + "," + p1x + "," + p1y);
					p2Writer.println("hit" + "," + p1x + "," + p1y);
					p1hits++;
				} else { //else a miss
					p1Writer.println("miss"  + "," + p1x + "," + p1y);
					p2Writer.println("miss"  + "," + p1x + "," + p1y);
				}
				/* ------------------player 2 turn----------------- */
				
				p2Writer.println("send messages");
				p1Writer.println("send messages");
				
				p1Messages = p1Reader.readLine();
				p2Messages = p2Reader.readLine();
				
				p2Writer.println("P1 said: " + p1Messages);
				p1Writer.println("P2 said: " + p2Messages);
				
				/* -----------------end of message cycle 1----------- */
				p2Writer.println("turn");
				p1Writer.println("wait");

				/* player1 attack */
				String p2Attack = p2Reader.readLine();
				String[] p2Coor = p2Attack.split(",");
				int p2x = Integer.parseInt(p2Coor[0]);
				int p2y = Integer.parseInt(p2Coor[1]);

				/* check if hit */
				if (logic.checkHit(false,p2x,p2y) == true) {
					p1Writer.println("hit" + "," + p2x + "," + p2y);
					p2Writer.println("hit" + "," + p2x + "," + p2y);
					p2hits++;
				} else { /* else a miss */
					p1Writer.println("miss" + "," + p2x + "," + p2y);
					p2Writer.println("miss" + "," + p2x + "," + p2y);
				}
				/* ------------------end player 2 turn----------------- */
				
				p1Writer.println("send messages");
				p2Writer.println("send messages");
				
				p1Messages = p1Reader.readLine();
				p2Messages = p2Reader.readLine();
				
				p2Writer.println("P1 said: " + p1Messages);
				p1Writer.println("P2 said: " + p2Messages);
				/* -----------------end message cycle 2------------------ */
				
				/* check if winner */
				if (p1hits == 31 || p2hits == 31) {
					gameOn = false;
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * readStream method is responsible for reading everything written to
	 * the server, and formatting it so that it can be properly utilized.
	 * 
	 * @param response
	 * 	string that was sent from user 
	 * @return
	 * 	formatted string representation of stream
	 */
	public String readStream(String response) {
		String finalResp = "";
		String splitResp[] = response.split(":");
		
		if (splitResp[0].equals("MOVE")) {
			finalResp = splitResp[1];
		} else if (splitResp[0].equals("MESSAGE")) {
			
		}
		
		return finalResp;
	}
	
	/**
	 * makeBoard creates a copy of any board by taking in a string which
	 * tells the which spots have a ship or not. 
	 * 
	 * @param placement
	 * 	string representing where there are ships on the board
	 * @return
	 * 	two dimensional array representing game board
	 */
	public int[][] makeBoard(String placement) {
		int[][] board = new int[10][10];
		String[] places = placement.split(";");
		int count = 0;
		
		for (int x = 0; x<10; x++) {
			for (int y = 0; y<10; y++) {
				board[x][y] = Integer.parseInt(places[count]);
				count++;
			}
		}
		
		return board;
	}

}
