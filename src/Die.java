public class Die {
	private int number = 0;
	
	public void roll () {
		number = (int) (Math.random() * 5) + 1;
	}
	
	public int getRoll () {
		return number;
	}	
}