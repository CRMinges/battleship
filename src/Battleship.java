
/**	An instance of Battleship creates a Ship with the characteristics of a Battleship.
 * 
 *	@author  mm****, cm****, wb****, gf****
 */
public class Battleship extends Ship {
	
	private static final int length = 4;
	
	/* constructor */
	public Battleship () {
		/* initializes ship to length 4 */
		super(length);
	}
	
	/* returns the type of ship */
	@Override
	public String getType() {
		return "Battleship";
	}

	@Override
	protected int getLength() {
		return length;
	}
	
}
