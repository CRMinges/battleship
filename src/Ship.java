
/** 
 * Class Ship is open for extension to create ships
 * of various sizes.
 * 	
 * @author mm****, cm****, wb****, gf****
 */
public abstract class Ship {
	
	private int length;			/* size of ship */
	
	public boolean isVertical;	/* true if ship oriented vertically */
	
	public Ship(int length) {
		this.length = length;
		isVertical = false;
	}
	
	abstract protected int getLength();
	
	abstract protected String getType();
	
} 
