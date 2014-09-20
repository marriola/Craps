import java.awt.Rectangle;
import java.awt.geom.Area;

class Field {
	/* class invariant:
	 * displayArea		contains the area where chips are displayed for the
	 * 					associated bet
	 * clickArea		contains the area where clicks are detected for the
	 * 					associated bet
	 * bet				the bet associated with this field
	 * split			how many ways this bet is split (default is 1)
	 */
	
	private Rectangle displayArea;
	private Area clickArea;
	private int bet, split;
	private boolean clickable, flash;
	
	public Field (Rectangle a, int i) {
		displayArea = a;
		clickable = true;
		clickArea = new Area(a);
		bet = i;
		split = 1;
		flash = false;
	}
	
	public Field (Rectangle a, int i, int s) {
		displayArea = a;
		clickable = true;
		clickArea = new Area(a);
		bet = i;
		split = s;
		flash = false;
	}
	
	public Rectangle getDisplayArea () {
		return displayArea;
	}
	
	public void setClickArea (Area area) {
		clickArea = area;
	}
	
	public void setClickable (boolean b) {
		clickable = b;
	}
	
	public boolean contains (int x, int y) {
		return clickable && clickArea.contains(x, y);
	}
	
	public int getBet () {
		return bet;
	}
	
	public int getSplit () {
		return split;
	}
	
	public boolean flash () {
		return flash = !flash;
	}
	
	public void setFlash (boolean f) {
		flash = f;
	}
	
	public boolean isFlashing () {
		return flash;
	}
}