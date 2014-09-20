import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Vector;

public class ConsoleCraps implements CrapsDriver {
	@SuppressWarnings("unused")
	private int INITIAL_MONEY = 100,
				money,
				dice[];
	boolean playing = true;

	static final String betNames[] = {"pass", "don't pass", "come", "don't come",
									  "2", "3", "yo", "12",
									  "hilo", "3way", "c", "e", "7",
									  "horn2", "horn3", "horn11", "horn12",
									  "whirl2", "whirl3", "whirl7", "whirl11", "whirl12",
									  "hop", "field", "hard4", "hard6", "hard8", "hard10",
									  "4", "5", "6", "8", "9", "10"};	
	static final int betIndices[] = {CrapsGame.BET_PASS, CrapsGame.BET_DONT_PASS,
									 CrapsGame.BET_COME, CrapsGame.BET_DONT_COME,
									 CrapsGame.SNAKE_EYES, CrapsGame.ACE_DEUCE,
									 CrapsGame.YO, CrapsGame.BOXCARS, CrapsGame.HI_LO,
									 CrapsGame.THREE_WAY, CrapsGame.CE_CRAPS,
									 CrapsGame.CE_YO, CrapsGame.SEVEN, CrapsGame.HORN_2,
									 CrapsGame.HORN_3, CrapsGame.HORN_11, CrapsGame.HORN_12,
									 CrapsGame.WHIRL_2,CrapsGame.WHIRL_3, CrapsGame.WHIRL_7,
									 CrapsGame.WHIRL_11, CrapsGame.WHIRL_12, CrapsGame.FIELD,
									 CrapsGame.HARD_4, CrapsGame.HARD_6, CrapsGame.HARD_8,
									 CrapsGame.HARD_10, CrapsGame.PLACE_4, CrapsGame.PLACE_5,
									 CrapsGame.PLACE_6, CrapsGame.PLACE_8, CrapsGame.PLACE_9,
									 CrapsGame.PLACE_10};
	static Scanner in = new Scanner(System.in);

	CrapsGame game;

	public ConsoleCraps (CrapsGame g) {
		game = g;
		money = INITIAL_MONEY;
	}
	
	void addChoice (Vector<Integer> allowed, int choice, String name) {
		System.out.print("[" + choice + "] " + name + " ");
		allowed.add(choice);
	}

	int betChoice () {
		Vector<Integer> allowed = new Vector<Integer>();
		boolean noBetYet = game.getBet(CrapsGame.BET_PASS) == 0 && game.getBet(CrapsGame.BET_DONT_PASS) == 0 && game.getPoint() == 0,
				noComeBet = game.getPoint() > 0 && game.getBet(CrapsGame.BET_COME) == 0 && game.getBet(CrapsGame.BET_DONT_COME) == 0 && game.getComePoint() == 0;

		addChoice(allowed, 0, "Do nothing");
		if (game.getMoneyOnTable() > 0)
			addChoice(allowed, 1, "Collect");
		if (noBetYet)
			addChoice(allowed, 2, "Pass");
		if (noBetYet)
			addChoice(allowed, 3, "Don't pass");
		if (noComeBet)
			addChoice(allowed, 4, "Come");
		if (game.getPoint() >= 4 && game.getPoint() <= 10 && game.getBet(CrapsGame.BET_COME) > 0)
			addChoice(allowed, 5, "Come odds");
		if (noComeBet)
			addChoice(allowed, 6, "Don't come");
		if (game.getPoint() >= 4 && game.getPoint() <= 10 && game.getBet(CrapsGame.BET_DONT_COME) > 0)
			addChoice(allowed, 7, "Don't come odds");
		if (game.getPoint() >= 4 && game.getPoint() <= 10 && game.getBet(CrapsGame.BET_PASS) > 0)
			addChoice(allowed, 8, "Pass odds");
		if (game.getPoint() >= 4 && game.getPoint() <= 10 && game.getBet(CrapsGame.BET_DONT_PASS) > 0)
			addChoice(allowed, 9, "Don't pass odds");
		addChoice(allowed, 10, "Single");
		addChoice(allowed, 11, "Multi");
		addChoice(allowed, 12, "Walk away");

		int choice = -1;
		while (!allowed.contains(choice)) {
			System.out.print("? ");
			try {
				choice = in.nextInt();
			} catch (InputMismatchException e) {
				in.next();	// throw away the offending token
			}
		}

		return choice;
	}

	void showPoint () {
		int point = game.getPoint(),
				comePoint = game.getComePoint();
		if (point == 0)
			System.out.println("The button is OFF");
		else
			System.out.println("The point is " + point);
		if (comePoint > 0 && game.getBet(CrapsGame.BET_COME) > 0)
			System.out.println("Come point is " + comePoint);
	}
	
	void show (String name, int bet) {
		int total = game.getBet(bet);
		
		if (total > 0) {
			System.out.print(name + "\t$" + total);
			int odds = game.getOdds(bet);
			if (odds > 0)
				System.out.println("\t$" + odds);
			else
				System.out.println("");
		}
		
	}
	
	void showBets (int start, int stop) {
		for (int i = start; i <= stop; i++)
			System.out.print(betNames[i] + "\t");
		System.out.println();
		for (int i = start; i <= stop; i++)
			System.out.printf("%.2f\t", game.getPayoff(i));
		System.out.println();
		for (int i = start; i <= stop; i++)
			System.out.printf("%d\t", game.getBetTotal(i));
		System.out.println("\n");
		
	}

	void showMoney () {
		System.out.println("You have $" + money);

		showBets(CrapsGame.SNAKE_EYES, CrapsGame.SEVEN);
		showBets(CrapsGame.HORN_2, CrapsGame.WHIRL_12);
		showBets(CrapsGame.HARD_4, CrapsGame.PLACE_10);

		show("Pass", CrapsGame.BET_PASS);
		show("Don't pass", CrapsGame.BET_DONT_PASS);
		show("Come", CrapsGame.BET_COME);
		show("Don't come", CrapsGame.BET_DONT_COME);
	}
	
	void pick (String name) {
		int i;
		for (i = 0; i < betNames.length; i++)
			if (name.equals(betNames[i])) {
				int j = betIndices[i];
				game.collect(j);
			}
	}

	void collect () {
		String name;
		boolean go = true;
		
		System.out.print("list (finish with period): ");

		while (go) {
			name = in.next();
			if (name.equals(".")) {
				for (int i = 0; i < CrapsGame.BET_TOTAL; i++) {
					game.collect(i);
				}
				break;
			} else if (name.charAt(name.length() - 1) == '.') {
				go = false;
				name = name.substring(0, name.length() - 1);
			}
			pick(name);
		}
		
		System.out.println();
	}

	int getBet () {
		int bet = 0;
		System.out.print("How much? ");
		while (in.hasNextInt())
			try {
				bet = in.nextInt();
				if (bet <= money)
					break;
			} catch (InputMismatchException e) { in.next(); }

		return bet;
	}

	void onTheHop (int bet) {
		System.out.print("On? ");
		int hop1 = in.nextInt(),
			hop2 = in.nextInt();
		game.placeHop(hop1, hop2, bet);
	}

	void singleRollBet (int bet) {
		System.out.print("[0] Snake eyes [1] Ace-deuce [2] Yo [3] 12 [4] Hi-lo [5] Three-way [6] C & E [7] Any seven [8] The horn [9] Whirl [10] On the hop [11] Field\nYour bet? ");
		int choice = in.nextInt();

		if (choice < 0 || choice > 11)
			return;
		else if (choice == 11)
			game.placeBet(CrapsGame.FIELD, bet);
		else if (choice == 10)
			onTheHop(bet);
		else if (choice == 6)
			game.placeBet(CrapsGame.CE_CRAPS, bet);
		else if (choice == 8)
			game.placeBet(CrapsGame.HORN_2, bet);
		else if (choice < 11)
			game.placeBet(CrapsGame.BET_SINGLE + choice, bet);
	}

	void multiRollBet (int bet) {
		System.out.print("[0] Hard four [1] Hard six [2] Hard eight [3] Hard ten [4-10] Place four/five/six/eight/ten\nYour bet? ");
		int choice = in.nextInt();

		if (choice < 0 || choice == 7 || choice > 10)
			return;
		else if (choice <= 3)
			game.placeBet(CrapsGame.HARD_4 + choice, bet);
		else if (choice == 4)
			game.placeBet(CrapsGame.PLACE_4, bet);
		else if (choice == 5)
			game.placeBet(CrapsGame.PLACE_5, bet);
		else if (choice == 6)
			game.placeBet(CrapsGame.PLACE_6, bet);
		else if (choice == 8)
			game.placeBet(CrapsGame.PLACE_8, bet);
		else if (choice == 9)
			game.placeBet(CrapsGame.PLACE_9, bet);
		else if (choice == 10)
			game.placeBet(CrapsGame.PLACE_10, bet);
	}
	
	void clear () {
		String out = "";
		out += (char)27 + "[r";
		for (int i = 0; i < 500; i++) out += "                    ";
		out += (char)27 + "[r";
		
		System.out.print(out);
	}
	
	public void setDice (int[] d) {
		dice = d;
	}

	public void setMoney (int amount) {
		money = amount;
	}
	
	public void addMoney (int amt) {
		money += amt;
	}
	
	public int getMoney () {
		return money;
	}
	
	public int getInitialMoney () {
		return INITIAL_MONEY;
	}
	
	public void setPlaying (boolean b) {
		playing = b;
	}
	
	public boolean isPlaying () {
		return playing;
	}
	
	public void beginTurn () {
		clear();
		showPoint();
		showMoney();		
		if (game.getPoint() == 0) System.out.println("Come-out roll");
	}

	public void endTurn () {
		if (playing) {
			System.out.println(game.getRollName() + "\n");
		
			for (int bet : game.getWinningBets())
				System.out.println(game.getBetName(bet) + ": you win");
		
			for (int bet : game.getLosingBets())
				System.out.println(game.getBetName(bet) + ": you lose");

			try {
				System.out.println("Press enter");
				System.in.read();
			} catch (Exception e) { }
		}
		
		String msg;
		int net = money + game.getMoneyOnTable() - INITIAL_MONEY;

		if (money + game.getMoneyOnTable() <= 0) {
			msg = "Get outta here, ya bum!";
			playing = false;
		} else if (net > 0)
			msg = "You made $" + net + ". Way to go, champ!";
		else if (net < 0)
			msg = "You lost $" + -net + ". Way to go, champ!";
		else
			msg = "You broke even.";
		System.out.println(msg);
	}
	
	public void play () {
		int choice = -1;

		while (choice == -1 || choice == 1) {
			if (choice == 1)
				beginTurn();
			
			choice = betChoice();
			switch (choice) {
				case 0:
					break;

				case 1:
					collect();
					System.out.println("");
					showMoney();
					break;
				
				case 2:
					game.placeBet(CrapsGame.BET_PASS, getBet());
					break;

				case 3:
					game.placeBet(CrapsGame.BET_DONT_PASS, getBet());
					break;

				case 4:
					game.placeBet(CrapsGame.BET_COME, getBet());
					break;

				case 5:
					game.placeOdds(CrapsGame.BET_COME, getBet());
					break;

				case 6:
					game.placeBet(CrapsGame.BET_DONT_COME, getBet());
					break;

				case 7:
					game.placeOdds(CrapsGame.BET_DONT_COME, getBet());
					break;

				case 8:
					game.placeOdds(CrapsGame.BET_PASS, getBet());
					break;

				case 9:
					game.placeOdds(CrapsGame.BET_DONT_PASS, getBet());
					break;

				case 10:
					singleRollBet(getBet());
					break;

				case 11:
					multiRollBet(getBet());
					break;

				case 12:
					playing = false;
					break;
			}
		} 
	}

	public static void main(String[] args) {
		CrapsGame game = new CrapsGame();
		CrapsDriver con = null;
		try {
			con = new ConsoleCraps(game);
			game.setDriver(con);
		} catch (Exception e) {
			System.exit(1);
		}

		while (con.isPlaying()) {
			con.beginTurn();
			con.play();
			if (con.isPlaying())
				con.setDice(game.roll());
			con.endTurn();			
		}
	}
}

