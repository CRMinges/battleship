/**
 * A Carrier is a Ship with length five.
 * Classes inherited from Ship include
 * hit() and isSunk().
 * 
 * @author mm****, cm****, wb****, gf****
 */
public class Carrier extends Ship {
	
	private static final int length = 5;
	
	public Carrier () {
		super(length);	/* ship length */
	
	}
	
	@Override
	protected String getType() {
		return "Carrier";
	}

	@Override
	protected int getLength() {
		return length;
	}
}
