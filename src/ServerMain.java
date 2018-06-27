import java.io.*;
import java.net.*;

/** ServerMain creates a server over which Battleship can be played by clients
 * 	who connect to the server.
 * 
 * @author  mm****, cm****, wb****, gf****
 */
public class ServerMain {

	public static volatile boolean shutdown_;
	public static ServerSocket socket_;

	/** The main() method opens a new server socket and waits for players
	 * 	to connect to it and creates a thread of ClientServicing to facilitate
	 * 	gameplay on the server.
	 * @param args
	 */
	public static void main ( String[] args ) {
		try {
			socket_ = new ServerSocket(1992);
			/* until shutdown request */
			while ( !shutdown_ ) {
				// create socket
				try {
					/* once an accept */
					Socket connect1 = socket_.accept();
					System.out.println("Waiting for second player...");
					Socket connect2 = socket_.accept();
					System.out.println("Connection completed");
					
					/* create clientService class, using connection as param */
					Thread thread = new Thread(new ClientServicing(connect1,connect2));

					thread.start();
					// start
				} catch ( IOException e ) {
					System.out.println(e.getMessage());
				}
			}
		} catch ( IOException e ) {
			System.out.println(e.getMessage());
		}
	}
	
} 
