
/**	An instance of Cruiser creates a Ship with the characteristics of a Cruiser.
 * 
 * @author  mm****, cm****, wb****, gf****
 */
public class Cruiser extends Ship {
	
	private static final int length = 3;
	
	/* constructor  */
	public Cruiser () {
		/* initializes ship to length 3 */
		super(length);
	}
	
	/* returns the type of ship */
	@Override
	public String getType() {
		return "Cruiser";
	}

	@Override
	protected int getLength() {
		return length;
	}
	
}