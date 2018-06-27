
/**	An instance of Destroyer creates a Ship with the characteristics of a Destroyer.
 * 
 * @author  mm****, cm****, wb****, gf****
 */
public class Destroyer extends Ship {

	private static final int length = 2;

	/* constructor */
	public Destroyer () {
		/* initializes ship of length 2 */
		super(length);
	}

	/* returns the type of ship */
	@Override
	public String getType() {
		return "Destroyer";
	}

	@Override
	protected int getLength() {
		return length;
	}

}
