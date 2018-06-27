
/**	An instance of Submarine creates a Ship with the characteristics of a Submarine.
 * 
 * @author  mm****, cm****, wb****, gf****
 */
public class Submarine extends Ship {

	private static final int length = 3;

	/* constructor */
	public Submarine () {
		/* initializes ship of length 3 */
		super(length);
	}

	/* returns the type of ship */
	@Override
	public String getType() {
		return "Submarine";
	}

	@Override
	protected int getLength() {
		return length;
	}

}
