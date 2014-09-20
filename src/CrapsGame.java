import java.util.Vector;

public class CrapsGame {
	/* roll			the total of the last dice rolled
	 * point		the current point. after a dice roll, point is set to the total.
	 * comePoint	the current come point. if a bet is placed on come or don't
	 * 				come, the come point is set to the total of the dice.
	 * bets			a list of all available bets on the table 
	 * hop1			the values to test for the on the hop bet. these will both
	 * hop2			be 0 if there is no on the hop bet on the table.
	 * die1			the dice rolled in each turn
	 * die2
	 * winningBets	a list of the winning and losing bets, updated after
	 * losingBets	rolling, and reset at the beginning of a turn.
	 */
	
	public static final int BET_PASS		= 0,
							BET_DONT_PASS	= 1,
							BET_COME		= 2,
							BET_DONT_COME	= 3,
							BET_SINGLE		= 4,
							BET_MULTI		= 24,

							SNAKE_EYES		= BET_SINGLE + 0,
			 				ACE_DEUCE		= BET_SINGLE + 1,
			 				YO				= BET_SINGLE + 2,
			 				BOXCARS			= BET_SINGLE + 3,
			 				HI_LO			= BET_SINGLE + 4,
			 				THREE_WAY		= BET_SINGLE + 5,
			 				CE_CRAPS		= BET_SINGLE + 6,
			 				CE_YO			= BET_SINGLE + 7,
			 				SEVEN			= BET_SINGLE + 8,
			 				HORN_2			= BET_SINGLE + 9,
			 				HORN_3			= BET_SINGLE + 10,
			 				HORN_11			= BET_SINGLE + 11,
			 				HORN_12			= BET_SINGLE + 12,
			 				WHIRL_2			= BET_SINGLE + 13,
			 				WHIRL_3			= BET_SINGLE + 14,
			 				WHIRL_7			= BET_SINGLE + 15,
			 				WHIRL_11		= BET_SINGLE + 16,
			 				WHIRL_12		= BET_SINGLE + 17,
			 				ON_THE_HOP		= BET_SINGLE + 18,
			 				FIELD			= BET_SINGLE + 19,
			 				
			 				HARD_4			= BET_MULTI + 0,
			 				HARD_6			= BET_MULTI + 1,
			 				HARD_8			= BET_MULTI + 2,
			 				HARD_10			= BET_MULTI + 3,
					 		PLACE_4			= BET_MULTI + 4,
					 		PLACE_5			= BET_MULTI + 5,
					 		PLACE_6			= BET_MULTI + 6,
					 		PLACE_8			= BET_MULTI + 7,
					 		PLACE_9			= BET_MULTI + 8,
					 		PLACE_10		= BET_MULTI + 9,
					 		
							BET_TOTAL		= PLACE_10 + 1;
	private static final String numbers[] = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve"};
	private static String rollName = "";

	private Bet[] bets;
	private int roll, point, comePoint, hop1, hop2;
	private Die die1, die2;
	private Vector<Integer> winningBets, losingBets;
	private CrapsDriver driver;
	
	/**
	 * Initializes a new craps game
	 */
	public CrapsGame () {
		reset();
	}
	
	/**
	 * Resets the game
	 * @postcondition The lists of winning and losing bets will be empty.
	 * 				  The driver's money will be reset to its initial value. 
	 * 				  All bets will be set up and with no money on them.
	 * 				  The point, come point, hop values, roll total, and dice
	 * 				  will be initialized.
	 */
	public void reset () {
		winningBets = new Vector<Integer>();
		losingBets = new Vector<Integer>();
		
		bets 				= new Bet[BET_TOTAL];
		bets[BET_PASS]		= new Bet("pass",		1, false, false, 0);
		bets[BET_DONT_PASS]	= new Bet("don't pass",	1, false, false, 0);
		bets[BET_COME]		= new Bet("come",		1, false, false, 0);
		bets[BET_DONT_COME] = new Bet("don't come",	1, false, false, 0);

		bets[SNAKE_EYES]	= new Bet("snake eyes",	30, true, false, 1, 2);
		bets[ACE_DEUCE]		= new Bet("ace-deuce",	15, true, false, 1, 3);
		bets[YO]			= new Bet("yo",			15, true, false, 1, 11);
		bets[BOXCARS]		= new Bet("boxcars",	30, true, false, 1, 12);
		bets[HI_LO]			= new Bet("hi-lo",		15, true, false, 2, 2, 12);
		bets[THREE_WAY]		= new Bet("three-way",	7, true, false, 3, 2, 3, 12);
		bets[CE_CRAPS]		= new Bet("C",			3, true, false, 3, 2, 3, 12);
		bets[CE_YO]			= new Bet("E",			7, true, false, 1, 11);
		bets[SEVEN]			= new Bet("7",			4, true, false, 1, 7);
		bets[HORN_2]		= new Bet("horn2",		27.0 / 4, true, false, 1, 2);
		bets[HORN_3]		= new Bet("horn3",		3, true, false, 1, 3);
		bets[HORN_11]		= new Bet("horn11",		3, true, false, 1, 11);
		bets[HORN_12]		= new Bet("horn12",		27.0 / 4, true, false, 1, 12);
		bets[WHIRL_2]		= new Bet("whirl2",		26.0 / 5, true, false, 1, 2);
		bets[WHIRL_3]		= new Bet("whirl3",		11.0 / 5, true, false, 1, 3);
		bets[WHIRL_7]		= new Bet("whirl7",		0, true, false, 1, 7);
		bets[WHIRL_11]		= new Bet("whirl11",	11.0 / 5, true, false, 1, 11);
		bets[WHIRL_12]		= new Bet("whirl12",	26.0 / 5, true, false, 1, 12);
		bets[ON_THE_HOP]	= new Bet("on the hop",	15, true, false, 0);
		bets[FIELD]			= new Bet("field",		1, true, false, 7, 2, 3, 4, 9, 10, 11, 12);
		
		bets[HARD_4]		= new Bet("hard4",		7, false, true, 1, 4, 7);
		bets[HARD_6]		= new Bet("hard6",		9, false, true, 1, 6, 7);
		bets[HARD_8]		= new Bet("hard8",		7, false, true, 1, 8, 7);
		bets[HARD_10]		= new Bet("hard10",		9, false, true, 1, 10, 7);
		bets[PLACE_4]		= new Bet("4",			9.0 / 5, false, false, 1, 4, 7);
		bets[PLACE_5]		= new Bet("5",			7.0 / 5, false, false, 1, 5, 7);
		bets[PLACE_6]		= new Bet("6", 			7.0 / 6, false, false, 1, 6, 7);
		bets[PLACE_8]		= new Bet("8", 			7.0 / 6, false, false, 1, 8, 7);
		bets[PLACE_9]		= new Bet("9", 			7.0 / 5, false, false, 1, 9, 7);
		bets[PLACE_10]		= new Bet("10", 		9.0 / 5, false, false, 1, 10, 7);		
		
		/* the driver may be null if it exists, but hasn't yet been set. if
		   this is the case, then the money should already be set */
		if (driver != null)
			driver.setMoney(driver.getInitialMoney());
		point = comePoint = 0;
		hop1 = hop2 = 0;
		die1 = new Die();
		die2 = new Die();
		roll = 0;
	}
	
	/**
	 * Sets the driver class associated with this game
	 */
	public void setDriver (CrapsDriver d) {
		driver = d;
	}
	
	/**
	 * @return The total amount rolled in the last turn
	 */
	public int getRoll () {
		return roll;
	}
	
	/**
	 * @return The name of the last total rolled
	 */
	public String getRollName () {
		return rollName;
	}

	/**
	 * @param bet The bet to check
	 * @return The amount placed on the bet
	 */
	public int getBet (int bet, boolean individual) {
		int total = 0;
		if (!individual && bet >= HORN_2 && bet <= HORN_12)
			for (int i = HORN_2; i <= HORN_12; i++)
				total += bets[i].get();
		else if (!individual && bet >= WHIRL_2 && bet <= WHIRL_12)
			for (int i = WHIRL_2; i <= WHIRL_12; i++)
				total += bets[i].get();
		else if (!individual && (bet == CE_CRAPS || bet == CE_YO))
			total = bets[CE_CRAPS].get() + bets[CE_YO].get();
		else
			total = bets[bet].get();
		
		return total;
	}
	
	public int getBet (int bet) {
		return getBet(bet, false);
	}
	
	/**
	 * @param bet The bet to check
	 * @return The odds placed behind the bet
	 */
	public int getOdds (int bet) {
		return bets[bet].getBehind();
	}
	
	/**
	 * @param bet The bet to check
	 * @return The total amount placed on the bet
	 */
	public int getBetTotal (int bet) {
		return getBet(bet) + getOdds(bet);
	}
	
	/**
	 * @param bet The bet to check
	 * @return The payoff associated with the bet
	 */
	public double getPayoff (int bet) {
		return bets[bet].getPayoff();
	}
	
	/**
	 * @param bet The bet to check
	 * @return The name of the bet
	 */
	public String getBetName (int bet) {
		return bets[bet].getName();
	}
	
	/**
	 * Collects a given bet
	 * @param bet The bet to collect
	 * @param amount The amount to collect
	 * @param odds The amount of odds to collect
	 * @postcondition amount will be subtracted from the main bet, and odds from
	 * 				  the odds. the sum of amount and odds will be added to the
	 * 				  driver's money.
	 */
	public void collect (int bet) {
		if (bet >= WHIRL_2 && bet <= WHIRL_12)
			collectSplitBet(bet, WHIRL_2, WHIRL_12);
		else if (bet >= HORN_2 && bet <= HORN_12)
			collectSplitBet(bet, HORN_2, HORN_12);
		else if (bet == CE_CRAPS || bet == CE_YO)
			collectSplitBet(bet, CE_CRAPS, CE_YO);
		else {
			collectBet(bet);
			collectOdds(bet);
		}
	}
	
	public void collectSplitBet (int click, int from, int to) {
		for (int i = from; i <= to; i++)
			collectBet(i);
	}
	
	public void collectBet (int bet) {
		int amount = bets[bet].get();
		driver.addMoney(amount);
		bets[bet].add(-amount);
	}
	
	public void collectOdds (int bet) {
		int amount = bets[bet].getBehind();
		driver.addMoney(amount);
		bets[bet].addBehind(-amount);		
	}
	
	/**
	 * Places a given bet
	 * @param num Bet to place
	 * @param bet Amount to place on bet
	 * @postcondition The given amount will be placed on the given bet. If a
	 * 				  pass, don't pass, come or don't come bet is being played,
	 * 				  the bet's winning and losing totals will be changed
	 * 				  appropriately. The amount of the bet will be subtracted
	 * 				  from the driver's money
	 */
	public void placeBet (int num, int bet) {
		if (driver.getMoney() < bet)
			return;
		driver.addMoney(-bet);
		
		if (num >= HORN_2 && num <= HORN_12)
			placeSplitBet(num, bet, HORN_2, HORN_12);
		else if (num >= WHIRL_2 && num <= WHIRL_12)
			placeSplitBet(num, bet, WHIRL_2, WHIRL_12);
		else if (num == CE_CRAPS || num == CE_YO)
			placeSplitBet(num, bet, CE_CRAPS, CE_YO);
		else {
			if ((num == BET_PASS && point == 0) || (num == BET_COME && comePoint == 0)) {
				bets[num].setWin(7, 11);
				bets[num].setLose(2, 3, 12);
			} else if ((num == BET_DONT_PASS && point == 0) || (num == BET_DONT_COME && comePoint == 0)) {
				bets[num].setWin(2, 3);
				bets[num].setLose(7, 11);
			}
			bets[num].add(bet);
		}
	}
	
	/**
	 * Place odds on a given bet
	 * @param num
	 * @param amount
	 */
	public void placeOdds (int num, int amount) {		
		if (driver.getMoney() < amount)
			return;
		driver.addMoney(-amount);
		bets[num].addBehind(amount);
	}
	
	/**
	 * Place a bet on the hop
	 * @param hop1 Die value to check for on the hop bet
	 * @param hop2 Die value to check for on the hop bet
	 * @param amount Bet to place
	 * @postcondition
	 */
	public void placeHop (int hop1, int hop2, int amount) {
		if (driver.getMoney() < amount)
			return;
		if (this.hop1 == 0) {
			this.hop1 = hop1;
			this.hop2 = hop2;
		}
		placeBet(ON_THE_HOP, amount);
	}
	
	public void placeSplitBet (int bet, int amount, int from, int to) {
		if (driver.getMoney() < bet)
			return;
		int split = to - from + 1,
			splitAmount = amount / split,
			remainder = amount % split;
		for (int i = from; i <= to; i++)
			bets[i].add(splitAmount);
		bets[bet].add(remainder);
	}

	/**
	 * Check all bets to see whether they won or lost
	 * @param die1 Value of first die
	 * @param die2 Value of second die
	 * @param total Dice total
	 * @param hard Whether the roll was hard or not (i.e., 4 + 4 = hard 8, 2 + 6 = easy 8)
	 */
	private void checkBets (int die1, int die2, int total, boolean hard) {
		for (int i = 0; i < bets.length; i++)
			if (i != ON_THE_HOP &&
			   (i < HARD_4 || (i >= HARD_4 && i <= PLACE_10 && point > 0)))
				switch (bets[i].checkBet(point,  total, hard)) {
					case 0:
						losingBets.add(i);
						break;
					case 1:
						winningBets.add(i);
						break;
				}

		if (bets[ON_THE_HOP].get() > 0) {
			if ((die1 == hop1 && die2 == hop2) || (die1 == hop2 && die2 == hop1)) {
				bets[ON_THE_HOP].multiply();
				if (hop1 == hop2)
					bets[ON_THE_HOP].multiply();
				winningBets.add(ON_THE_HOP);
			} else {
				losingBets.add(ON_THE_HOP);
				bets[ON_THE_HOP].reset();
			}
		}
		
		if ((total == 2 || total == 12) && bets[FIELD].get() > 0)
			bets[FIELD].add(bets[FIELD].get() / 2);
		 
		if (point == 0 && bets[BET_PASS].get() + bets[BET_DONT_PASS].get() > 0) {
			bets[BET_PASS].setWin(total);
			bets[BET_PASS].setLose(7);

			bets[BET_DONT_PASS].setWin(7);
			bets[BET_DONT_PASS].setLose(total);
		}
		
		if (comePoint == 0 && bets[BET_COME].get() + bets[BET_DONT_COME].get() > 0) {
			if (total >= 4 && total <= 10 && total != 7) {
				bets[BET_COME].setWin(total);
				bets[BET_COME].setLose(7);
				bets[BET_DONT_COME].setWin(7);
				bets[BET_DONT_COME].setLose(total);
			}
		}
	}
	
	/**
	 * Rolls the dice
	 * @postcondition All bets will be calculated, and the point will be set to
	 * 				  the total of the roll if the roll does not immediately
	 * 				  win or lose. If there is a bet on come or don't come and
	 * 				  the come point is 0, the come point will be set.
	 * @return The values of the dice rolled
	 */
	public int[] roll () {
		winningBets = new Vector<Integer>();
		losingBets = new Vector<Integer>();
		
		die1.roll(); die2.roll();
		int num1 = die1.getRoll(),
			num2 = die2.getRoll(),
			total = num1 + num2;
		boolean hard = (num1 == num2),
				even = total % 2 == 0;

		rollName = (even ? (hard ? "hard " : "easy ") : "") + numbers[total - 1];
		checkBets(num1, num2, total, hard);

		if (point > 0 && (total == 7 || total == point))
			point = 0;
		else if (point == 0 && total >= 4 && total <= 10 && total != 7)
			point = total;

		if (comePoint > 0 && (total == 7 || total == comePoint))
			comePoint = 0;
		else if (comePoint == 0 && total >= 4 && total <= 10 && total != 7 && bets[BET_COME].get() + bets[BET_DONT_COME].get() > 0)
			comePoint = total;
		
		roll = total;
		int dice[] = {num1, num2};
		
		hop1 = hop2 = 0;
		return dice;
	}
	
	/**
	 * @return The game point
	 */
	public int getPoint () {
		return point;
	}
	
	/**
	 * @return The come point
	 */
	public int getComePoint () {
		return comePoint;
	}
	
	/**
	 * @return The totals of all bets placed
	 */
	public int getMoneyOnTable () {
		int total = 0;
		for (int i = 0; i < BET_TOTAL; i++)
			if (bets[i] != null)
				total += bets[i].getTotal();
		return total;
	}
	
	/**
	 * @return The bets that won in the last turn
	 */
	public Vector<Integer> getWinningBets () {
		return winningBets;
	}
	
	/**
	 * @return The bets that lost in the last turn
	 */
	public Vector<Integer> getLosingBets () {
		return losingBets;
	}
}
