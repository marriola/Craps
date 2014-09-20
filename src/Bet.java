import java.util.Vector;

public class Bet {
	/* class invariant:
	 * bet			the amount placed on this bet
	 * behind		the odds placed behind this bet
	 * payoff		the proportion of winnings to the original bet
	 * name			the name of the bet
	 * winTotal		the dice totals that result in winning the bet
	 * loseTotal	the dice totals that result in losing the bet
	 * singleRoll	if false, the bet is a multi-roll bet and continues until
	 * 				one of the values in loseTotal is encountered.
	 * 				if true, the bet is a single-roll bet. it will win if
	 * 				one of the values in winTotal is encountered, and lose
	 *				otherwise.
	 */

	static final public boolean DONTPASS = true;

	private int bet, behind;
	private double payoff;
	private String name;
	
	private Vector<Integer> winTotal, loseTotal;
	private boolean singleRoll, winHard;
	
	public Bet (String name, double payoff, boolean single, boolean hard, int numWinning, int... total) {
		this.name = name;
		bet = behind = 0;
		this.payoff = payoff;
		winHard = hard;
		singleRoll = single;
		
		winTotal = new Vector<Integer>();
		loseTotal = new Vector<Integer>();
		
		for (int i = 0; i < numWinning; i++)
			winTotal.add(total[i]);
		for (int i = numWinning; i < total.length; i++)
			loseTotal.add(total[i]);
	}
	
	public void setWin (int... list) {
		Vector<Integer> newList = new Vector<Integer>();
		for (int i = 0; i < list.length; i++)
			newList.add(list[0]);
		winTotal = newList;
	}
	
	public void setLose (int... list) {
		Vector<Integer> newList = new Vector<Integer>();
		for (int i = 0; i < list.length; i++)
			newList.add(list[i]);
		loseTotal = newList;
	}
	
	public void reset () {
		bet = behind = 0;
	}
	
	public void add (int value) {
		bet += value;
		if (bet < 0)
			bet = 0;
	}
	
	public void addBehind (int value) {
		behind += value;
		if (behind < 0)
			behind = 0;
	}
	
	public void addPoint (int value) {
		winTotal.add(value);
	}
	
	public int get () {
		return bet;
	}
	
	public String getName () {
		return name;
	}
	
	public int getBehind () {
		return behind;
	}
	
	public int getTotal () {
		return bet + behind;
	}
	
	public double getPayoff () {
		return payoff;
	}
	
	public void multiply () {
		bet = (int) Math.round(bet * (1 + payoff));
	}
	
	public void multiplyBehind (int point) {
		multiplyBehind(point, false);
	}
	
	public void multiplyBehind (int point, boolean mode) {
		double value = 0;

		if (point == 4 || point == 10)
			value = 2.0;
		else if (point == 5 || point == 9)
			value = 3.0 / 2;
		else if (point == 6 || point == 8)
			value = 6.0 / 5;
		
		if (mode)
			value = 1 / value;

		behind = (int) Math.round(behind * (1 + value));
	}
	
	public int checkBet (int point, int total, boolean hard) {
		boolean win, lose;
		if (bet > 0) {
			win = winTotal.contains(total);
			lose = loseTotal.contains(total) ||
				   (singleRoll && !winTotal.contains(total)) ||
				   (winHard && !hard && winTotal.contains(total));

			if (win && !lose) {
				//System.out.println(name + ": you win");
				multiply();
				multiplyBehind(point);
				return 1;
			} else if (lose) {
				//System.out.println(name + ": you lose");
				reset();
				return 0;
			}			
		}
		return -1;
	}
}
