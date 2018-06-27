import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * This is the graphical display for a player. BattleshipGUI has two windows, 
 * a “welcome” and a game window. The welcome window allows a player to 
 * select options for their game, while the game window is where Battleship 
 * is carried out.
 * 
 * @author mm****, cm****, wb****, gf****
 */
public class BattleshipGUI extends JFrame {
	private JFrame welcome_;				/* welcome window */
	private JFrame game_;						/* game window	 */
	private int[][] boardAttack;		/* attacking board */
	private int[][] boardObserve;		/* player's own board */

	int shipCounter_;							/* number of ships */
	int[] numShips_;								/* number of each type of ship */
	boolean placeShip_;							/* if a player is trying to place a ship */
	boolean legalPlace_;						/* if a ships placement is allowed */

	int currMouseX;								/* current mouse x location	 */
	int currMouseY;								/* current mouse y location */
	int clickX;										/* x location on click */
	int clickY;										/* y location on click */
	boolean mouseClicked_;					/* true on click */

	boolean online_;								/* true for online play */
	boolean offline_;							/* true for offline play */

	String server_;
	Ship ship_; 
	Sound gameSound_;							/* milos's sound object */
	String outgoingMessage_;
	JTextField chat_;
	JTextField ipfield_;

	public BattleshipGUI () {
		shipCounter_ = 0;
		numShips_ = new int[5];
		mouseClicked_ = false;
		placeShip_ = false;
		legalPlace_ = false;
		boardAttack = new int[10][10];
		boardObserve = new int[10][10];
		online_ = false;
		offline_ = false;
		server_ = "";
		ship_ = new Battleship();
		outgoingMessage_ = "";
		for ( int r = 0 ; r < 10 ; r++ ) {
			for ( int c = 0 ; c < 10 ; c++ ) {
				boardAttack[r][c] = 0;
				boardObserve[r][c] = 0;
			}
		}
		for ( int i = 0 ; i < numShips_.length ; i++ ) {
			numShips_[i] = 0;
		}

		welcomeScreen();

		gameScreen();
	}

	/**
	 * Draws the game board, ship graphics, and hit graphics
	 * using paintComponent. 
	 */
	private class BoardPanel extends JPanel {

		public BoardPanel () {
			super();
			addMouseMotionListener(new BoardPanelListener());
			addMouseListener(new BoardMouseListener());
			this.setBackground(Color.BLACK);
			setVisible(true);
		}

		@Override
		public void paintComponent ( Graphics g ) {
			super.paintComponent(g);
			g.setColor(new Color(123,163,106));

			/* drawing the game board */
			for ( int i = 0 ; i < 10 ; i++ ) {
				for ( int j = 0 ; j < 10 ; j++ ) {
					g.drawRect(i * 40 + 5,j * 40 + 5,40,40);
				}
			}
			for ( int i = 0 ; i < 10 ; i++ ) {
				for ( int j = 0 ; j < 10 ; j++ ) {
					g.drawRect(i * 40 + 588,j * 40 + 5,40,40);

				}
			}

			/* draw observe board with ships, hits, and misses */
			for ( int row = 0 ; row < boardObserve.length ; row++ ) {
				for ( int col = 0 ; col < boardObserve[0].length ; col++ ) {
					if ( boardObserve[row][col] == 1 ) {	// ship
						g.fillRect((589 + row * 40),(6 + col * 40),40,40);
					}
					if ( boardObserve[row][col] == 2 ) {	//hit
						drawHit(g, (591 + row * 40), (8 + col * 40));
					}
					if ( boardObserve[row][col] == 3 ) {	//miss
						drawMiss(g, (591 + row * 40), (8 + col * 40));
					}
				}
			}

			/* draw attack board with hits and misses */
			for ( int row = 0 ; row < boardAttack.length ; row++ ) {
				for ( int col = 0 ; col < boardAttack[0].length ; col++ ) {
					if ( boardAttack[row][col] == 2 ) {	//hit
						drawHit(g,  (8 + row * 40), (8 + col * 40));
					}
					if ( boardAttack[row][col] == 3 ) {	//miss
						drawMiss(g, (8 + row * 40), (8 + col * 40));
					}
				}
			}

			/* if mouse is over attack board, draw a red reticle */
			if ( (currMouseX > 6 && currMouseX < 406)
					&& (currMouseY > 8 && currMouseY < 408) ) {

				/* fine tuning of x, y locations */
				int x = currMouseX - 6;
				int y = currMouseY - 8;

				/* reticle drawing */
				g.setColor(Color.RED);
				g.drawOval((x / 40) * 40 + 6,(y / 40) * 40 + 6,37,37);
				g.drawLine((x / 40) * 40 + 25,(y / 40) * 40 + 6,(x / 40) * 40 + 25,
				           (y / 40) * 40 + 42);
				g.drawLine((x / 40) * 40 + 7,(y / 40) * 40 + 25,(x / 40) * 40 + 42,
				           (y / 40) * 40 + 25);
			}

			/* if the user is trying to place their ships */
			if ( placeShip_ ) {
				int size = ship_.getLength() * 40 - 3;
				g.setColor(Color.CYAN);

				/* if mouse is over observe board */
				if ( (currMouseX > 590 && currMouseX < 990)
						&& (currMouseY > 8 && currMouseY < 408) ) {

					/* fine tuning of x, y locations */
					int x = currMouseX + 10;
					int y = currMouseY - 10;
					int ex = (currMouseX-589)/40;
					int ey = (currMouseY-10)/40;
					
					if ( ship_.isVertical ) { /* if ship is vertical */
						if ( ((y / 40) * 40 - 10) + size < 408 ) {
							if (isLegalPlace(ex,ey, ship_.getLength() ,true)) {
								g.fillRect((x / 40) * 40 - 10,(y / 40) * 40 + 7,37,size);
								legalPlace_ = true;
							} else {
								g.setColor(Color.RED);
								g.fillRect((x / 40) * 40 - 10,(y / 40) * 40 + 7,37,size);
								legalPlace_ = false;
							}
						} else {
							legalPlace_ = false;
						}
					}else { 									/*else ship is horizontal*/
						if ( ((x / 40) * 40 - 10) + size < 990 ) {
							if (isLegalPlace(ex,ey,ship_.getLength(),false)) {
								g.fillRect((x / 40) * 40 - 10,(y / 40) * 40 + 7,size,37);
								legalPlace_ = true;
							} else {
								g.setColor(Color.RED);
								g.fillRect((x / 40) * 40 - 10,(y / 40) * 40 + 7,size,37);
								legalPlace_ = false;
							}
						} else {
							legalPlace_ = false;
						}
					}

				}
			}
			repaint();

		}
	}

	public boolean isLegalPlace(int x, int y, int length, boolean isVertical) {
		for (int i = 0; i < length; i++) {
			if (isVertical && boardObserve[x][y+i] == 1) {
					return false;
			} else if (!isVertical && boardObserve[x+i][y] == 1){
					return false;
			}
			
		}

		return true;
	}

	/* used to track mouse clicks on the game window */
	private class BoardMouseListener implements MouseListener {
		@Override
		public void mouseClicked ( MouseEvent e ) {}

		@Override
		public void mousePressed ( MouseEvent e ) {}

		@Override
		public void mouseReleased ( MouseEvent e ) {

			if ( shipCounter_ >= 10 ) {
				mouseClicked_ = true;
				clickX = e.getX();
				clickY = e.getY();
			}

			// toggle vertical ship placement on right click */
			if ( placeShip_ && SwingUtilities.isRightMouseButton(e) ) {
				ship_.isVertical = !ship_.isVertical;
			} 

			/* if trying to place and legal place are true, allow 
			 * the board state to be updated. */
			else if ( placeShip_ && legalPlace_ ) {
				int x = (currMouseX + 10) / 40 - 15;
				int y = (currMouseY - 8) / 40;
				placeShip(ship_,x,y);
				shipCounter_++;
				legalPlace_ = false;
				placeShip_ = false;
			}
		}
		@Override
		public void mouseEntered ( MouseEvent e ) {}
		@Override
		public void mouseExited ( MouseEvent e ) {}
	}

	/* used to track mouse movement on the game window */
	private class BoardPanelListener implements MouseMotionListener {
		@Override
		public void mouseDragged ( MouseEvent e ) {}

		@Override
		public void mouseMoved ( MouseEvent e ) {
			currMouseX = e.getX();
			currMouseY = e.getY();
//			int ex = (currMouseX-589)/40;
//			int ey = (currMouseY-6)/40;
			//g.fillRect((589 + row * 40),(6 + col * 40),40,40);
			//System.out.println(ex + ", " + ey);
		}
	}

	/**
	 * Places a ship on the observe panel with a starting 
	 * x and y location. 
	 * 
	 * @param ship: the ship to be placed
	 * @param x: beginning x location of ship
	 * @param y: beginning y location of ship
	 */
	private void placeShip ( Ship ship, int x, int y ) {
		// if vertical, mark the column as containing a ship
		if ( ship.isVertical ) {
			for ( int i = 0 ; i < ship.getLength() ; i++ ) {
				boardObserve[x][y + i] = 1;
			}
		} 
		/* otherwise, mark the row as containing a ship */
		else {
			for ( int i = 0 ; i < ship.getLength() ; i++ ) {
				boardObserve[x + i][y] = 1;
			}
		}
		if ( shipCounter_ == 10 ) {
			chat_
			.setText("Waiting for opponent to place their Ships. Stand by, Captain!");
		} else {
			chat_.setText(ship_.getType() + " placed!" + "\n	  Destroyers left: "
					+ (3 - numShips_[0]) + ", Cruisers left: " + (2 - numShips_[1])
					+ ", Submarines left: " + (2 - numShips_[2]) + ", Battleships left: "
					+ (2 - numShips_[3]) + ", Carriers left: " + (1 - numShips_[4]));
		}
	}

	/**
	 * This is the pop-up window before game begins. 
	 * Depending on online or offline options, a new 
	 * window will appear in place of this one. See 
	 * the private methods localOptions() and 
	 * networkOptions() for details on those windows.
	 */
	private void welcomeScreen () {
		welcome_ = new JFrame();
		JPanel welcomePanel = new JPanel(new BorderLayout());

		/* Battleship image added into JLabel */
		Image welcomeImage = null;
		try {
			welcomeImage = ImageIO.read(getClass().getResource("picBattleship.png"));
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		JLabel label = new JLabel();
		label.setIcon(new ImageIcon(welcomeImage));

		JPanel optionPanel = new JPanel();
		JLabel selectOne = new JLabel("Select an Opponent: ");
		JButton computer = new JButton("Computer");
		computer.setFocusable(false);
		computer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed ( ActionEvent arg0 ) {
				JOptionPane.showMessageDialog(welcome_,"This feature is not yet supported!");
				//				welcome_.setVisible(false);		/* change windows */
				//				welcome_ = localOptions();		/* display local options */
				//				offline_ = true;							/* offline play is true */
			}
		});
		JButton player = new JButton("Player");
		player.setFocusable(false);
		player.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed ( ActionEvent arg0 ) {
				welcome_.setVisible(false);		/* change windows */
				welcome_ = networkOptions();	/* display online options */
				online_ = true;							/* online play is true */
			}
		});

		optionPanel.add(selectOne);
		optionPanel.add(computer);
		optionPanel.add(player);

		welcomePanel.add(label,BorderLayout.CENTER);
		welcomePanel.add(optionPanel,BorderLayout.SOUTH);

		welcome_.setContentPane(welcomePanel);
		welcome_.setSize(300,200);
		welcome_.pack();
		welcome_.setLocationRelativeTo(null);
		welcome_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		welcome_.setResizable(false);
		welcome_.setVisible(true);
	}

	/**
	 * This is the game window where Battleship takes place.
	 * The window contains two playing boards, buttons to 
	 * place ships, a message box, and quit, new game, send 
	 * buttons. 
	 */
	private void gameScreen () {
		game_ = new JFrame();
		JPanel content = new JPanel(new BorderLayout());

		BoardPanel gameBoard = new BoardPanel(); /* game board */
		content.add(gameBoard,BorderLayout.CENTER);

		/* contains two panels: northComps and southComps */
		JPanel gameComponents = new JPanel(new BorderLayout());

		/* northComps holds shipPanel (buttons for ships), JLabel for */
		JPanel northComps = new JPanel();
		BoxLayout northLayout = new BoxLayout(northComps,BoxLayout.PAGE_AXIS);
		northComps.add(Box.createHorizontalGlue());
		northComps.setLayout(northLayout);
		northComps.setAlignmentX(0);

		JPanel shipPanel = new JPanel(new FlowLayout());

		/* ship pictures added to JButtons */
		Image destroyerPic = null;
		Image cruiserPic = null;
		Image submarinePic = null;
		Image battleshipPic = null;
		Image carrierPic = null;
		try {
			destroyerPic = ImageIO.read(getClass().getResource("patrol.gif"));
			cruiserPic = ImageIO.read(getClass().getResource("battleship.gif"));
			submarinePic = ImageIO.read(getClass().getResource("submarine.gif"));
			battleshipPic = ImageIO.read(getClass().getResource("seawolf.gif"));
			carrierPic = ImageIO.read(getClass().getResource("carrier.gif"));
		} catch ( IOException e1 ) {
			e1.printStackTrace();
		}

		JButton destroyer = new JButton();
		destroyer.setIcon(new ImageIcon(destroyerPic));
		destroyer.setFocusable(false);
		destroyer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed ( ActionEvent arg0 ) {
				ship_ = new Destroyer();		/* ship set to destroyer */
				placeShip_ = true;				/* user wants to place */
				numShips_[0]++;					/* destroyer incremented */
				if ( numShips_[0] == 3 ) { 
					destroyer.setEnabled(false);
				}
			}
		});

		JButton cruiser = new JButton();
		cruiser.setIcon(new ImageIcon(cruiserPic));
		cruiser.setFocusable(false);
		cruiser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed ( ActionEvent arg0 ) {
				ship_ = new Cruiser();			/* ship set to cruiser */
				placeShip_ = true;				/* user wants to place */
				numShips_[1]++;					/* cruiser incremented */
				if ( numShips_[1] == 2 ) { 
					cruiser.setEnabled(false);
				}
			}
		});

		JButton submarine = new JButton();
		submarine.setIcon(new ImageIcon(submarinePic));
		submarine.setFocusable(false);
		submarine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed ( ActionEvent arg0 ) {
				ship_ = new Submarine();		/* ship set to submarine */
				placeShip_ = true;				/* user wants to place */
				numShips_[2]++;					/* submarine incremented */
				if ( numShips_[2] == 2 ) {
					submarine.setEnabled(false);
				}
			}
		});

		JButton battleship = new JButton();
		battleship.setIcon(new ImageIcon(battleshipPic));
		battleship.setFocusable(false);
		battleship.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed ( ActionEvent arg0 ) {
				ship_ = new Battleship();		/* ship set to battleship */
				placeShip_ = true;				/* user wants to place */
				numShips_[3]++;					/* battleship incremented */
				if ( numShips_[3] == 2 ) {
					battleship.setEnabled(false);
				}
			}
		});

		JButton carrier = new JButton();
		carrier.setIcon(new ImageIcon(carrierPic));
		carrier.setFocusable(false);
		carrier.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed ( ActionEvent arg0 ) {
				ship_ = new Carrier();			/* ship set to Carrier */
				placeShip_ = true;				/* user wants to place */
				numShips_[4]++;					/* carrier incremented */
				if ( numShips_[4] == 1 ) { 
					carrier.setEnabled(false);
				}
			}
		});

		/* add ship buttons to panel */
		shipPanel.add(destroyer,BorderLayout.CENTER);
		shipPanel.add(cruiser,BorderLayout.CENTER);
		shipPanel.add(submarine,BorderLayout.CENTER);
		shipPanel.add(battleship,BorderLayout.CENTER);
		shipPanel.add(carrier,BorderLayout.CENTER);

		northComps.add(shipPanel);

		JLabel chatLabel = new JLabel("MESSAGE BOARD:");
		northComps.add(chatLabel);

		chat_ = new JTextField("Welcome to Battleship!"
				+ "\nBegin by clicking the buttons to place your Ships! ");
		chat_.setEnabled(true);
		chat_.setRequestFocusEnabled(false);
		chat_.setPreferredSize(new Dimension(50,75));
		northComps.add(chat_);

		gameComponents.add(northComps,BorderLayout.NORTH);

		/* southComps holds two JButtons, newGame and quit */
		JPanel southComps = new JPanel(new FlowLayout());

		JButton quit = new JButton("Quit Game");
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed ( ActionEvent arg0 ) {
				String question;
				question = "Are you sure that you want to exit?";
				int result = JOptionPane.showConfirmDialog(null,question,null,
				                                           JOptionPane.YES_NO_OPTION);
				if ( result == JOptionPane.YES_OPTION ) {
					System.exit(0);
				}
			}
		});

		JButton send = new JButton("Send");
		send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed ( ActionEvent e ) {
				outgoingMessage_ = chat_.getText();
				chat_.setText("");
			}
		});
		southComps.add(quit);
		southComps.add(send);

		gameComponents.add(southComps,BorderLayout.SOUTH);

		content.add(gameComponents,BorderLayout.SOUTH);

		game_.setContentPane(content);
		game_.setSize(1000,700);
		game_.setResizable(false);
		game_.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * This returns a JFrame object that is set as a 
	 * new welcome_ window. Local options window will 
	 * include game options and a start button. Once 
	 * the start button is clicked, the game window is 
	 * set visible. 
	 * 
	 * @return: JFrame containing options for local play
	 */
	//	private JFrame localOptions () {
	//		JFrame frame = new JFrame("Please Select Rules");
	//		JPanel panel = new JPanel(new BorderLayout());
	//
	//		JPanel radioPanel = new JPanel();
	//		final JRadioButton button1 = new JRadioButton("Normal Game");
	//		final JRadioButton button2 = new JRadioButton("Timer Game");
	//		button1.setSelected(true);
	//		button1.addActionListener(new ActionListener() {
	//			@Override
	//			public void actionPerformed ( ActionEvent arg0 ) {
	//				button1.setSelected(true);
	//				button2.setSelected(false);
	//			}
	//		});
	//		button2.addActionListener(new ActionListener() {
	//			@Override
	//			public void actionPerformed ( ActionEvent arg0 ) {
	//				button1.setSelected(false);
	//				button2.setSelected(true);
	//			}
	//		});
	//		radioPanel.add(button1);
	//		radioPanel.add(button2);
	//
	//		Image introImage = null;
	//		try {
	//			introImage = ImageIO.read(getClass().getResource("/resources/picBattleship2.png"));
	//		} catch ( IOException e ) {
	//			e.printStackTrace();
	//		}
	//		JLabel label = new JLabel();
	//		label.setIcon(new ImageIcon(introImage));
	//
	//		JButton start = new JButton("Start!");
	//		start.setFocusable(false);
	//		start.addActionListener(new ActionListener() {
	//			@Override
	//			public void actionPerformed ( ActionEvent arg0 ) {
	//				welcome_.setVisible(false);
	//				game_.setVisible(true);
	//			}
	//		});
	//
	//		panel.add(radioPanel,BorderLayout.NORTH);
	//		panel.add(label,BorderLayout.CENTER);
	//		panel.add(start,BorderLayout.SOUTH);
	//
	//		frame.setContentPane(panel);
	//		frame.setSize(300,200);
	//		frame.setLocationRelativeTo(null);
	//		frame.setResizable(false);
	//		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
	//		frame.setVisible(true);
	//		return frame;
	//	}

	/**
	 * This returns a JFrame object that is set as a
	 * new welcome_ window. Network options window will 
	 * include game options, an IP address to connect to, 
	 * and a start button. Once the start button is 
	 * clicked, the game window is set visible. 
	 * 
	 * @return: JFrame containing options for online play
	 */
	private JFrame networkOptions () {
		JFrame frame = new JFrame("Please Select Rules");
		JPanel panel = new JPanel(new BorderLayout());

		JPanel radioPanel = new JPanel();
		final JRadioButton button1 = new JRadioButton("Normal Game");
		//		final JRadioButton button2 = new JRadioButton("Timer Game");
		button1.setSelected(true);
		button1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed ( ActionEvent arg0 ) {
				button1.setSelected(true);
				//				button2.setSelected(false);
			}
		});
		//		button2.addActionListener(new ActionListener() {
		//			@Override
		//			public void actionPerformed ( ActionEvent arg0 ) {
		//				button1.setSelected(false);
		//				button2.setSelected(true);
		//			}
		//		});
		radioPanel.add(button1);
		//		radioPanel.add(button2);

		Image introImage = null;
		try {
			introImage = ImageIO.read(getClass().getResource("picBattleship2.png"));
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		JLabel label = new JLabel();
		label.setIcon(new ImageIcon(introImage));

		JPanel optionPanel = new JPanel();

		JButton start = new JButton("Start!");
		start.setFocusable(false);
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed ( ActionEvent arg0 ) {
				server_ = ipfield_.getText();
				welcome_.setVisible(false);
				game_.setVisible(true);
			}
		});

		JLabel ip = new JLabel("IP:");
		ipfield_ = new JTextField(15);
		ipfield_.add(new JLabel("Enter your IP address:"));

		optionPanel.add(ip);
		optionPanel.add(ipfield_);
		optionPanel.add(start);

		panel.add(radioPanel,BorderLayout.NORTH);
		panel.add(label,BorderLayout.CENTER);
		panel.add(optionPanel,BorderLayout.SOUTH);

		frame.setContentPane(panel);
		frame.setSize(320,200);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
		return frame;
	}

	public void setMessage ( String message ) {
		outgoingMessage_ = message;
	}

	public String getMessage () {
		return outgoingMessage_;
	}

	public boolean getClick () {
		return mouseClicked_;
	}

	public void setClick ( boolean bool ) {
		mouseClicked_ = bool;
	}

	public int[][] getBoard () {
		return boardObserve;
	}

	public boolean shipsPlaced () {
		if ( shipCounter_ == 10) {
			return true;
		}
		return false;
	}

	/** draws a nice and colorful explosion graphic at the 
	 *  given x, y location
	 */
	private void drawHit(Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g;
		Image fire = null;

		//		fire = Toolkit.getDefaultToolkit().getImage("resources/hit.gif");
		try {
			fire = ImageIO.read(getClass().getResource("hit.gif"));
		} catch ( IOException e ) {
			e.printStackTrace();
		} 
		g2.drawImage(fire, x, y, this);
		g2.finalize();
	}

	/** draws a nice and colorful water dropplet (indicating
	 * a shot missing a battleship) at the given x, y location
	 */
	private void drawMiss(Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g;
		Image splash = null;
		try {
			splash = ImageIO.read(getClass().getResource("water.gif"));
		} catch ( IOException e ) {
			e.printStackTrace();
		}

		//splash = Toolkit.getDefaultToolkit().getImage("resources/water.gif");
		g2.drawImage(splash, x, y, this);
		g2.finalize();
	}

	public void setObserve(int state, int x, int y) {
		if ((x <= 10 && x >= 0) && (y <= 10 && y >= 0)){
			if (state == 2) {
				//				gameSound_.thunder.play();
				boardObserve[x][y] = state;
			}
			if (state == 3) {
				//				gameSound_.splash.play();
				boardObserve[x][y] = state;
			}
		}
	}

	public void setAttack(int state, int x, int y) {
		if ((x <= 10 && x >= 0) && (y <= 10 && y >= 0)){
			if (state == 2) {
				//				gameSound_.thunder.play();
				boardAttack[x][y] = state;
			}
			if (state == 3) {
				//				gameSound_.splash.play();
				boardAttack[x][y] = state;
			}
		}
	}
}

