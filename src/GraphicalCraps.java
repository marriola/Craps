import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.*;
import java.util.*;
import java.util.jar.*;
import java.util.concurrent.Semaphore;

public class GraphicalCraps extends JPanel implements CrapsDriver, ActionListener, ChangeListener {
	/* used by the game:
	 * INITIAL_MONEY	amount of money player starts with
	 * money			amount of money player currently has
	 * dice				values of each die rolled
	 * game				the game object that manages the dice and bets
	 * betAmount		amount to place on the table per click
	 * betPlaced		number of bets placed this turn
	 * hop1				values to test for with on the hop bet
	 * hop2
	 * 
	 * used by the driver:
	 * fields			contains a list of areas where bets are placed and displayed
	 * shoot			this semaphore will be available at the beginning of a turn.
	 * 					when the "Shoot" button is pressed, it must be acquired in
	 *					order to waiting game loop to end and allow the game object
	 *					to roll the dice and decide the outcome of the bets
	 * crapsTable		the images used during the game
	 * chip
	 * onButton
	 * offButton
	 * font				font used for displaying text
	 * metrics			font metrics object used for centering text
	 */
	
	private static final long serialVersionUID = -8264380666227973590L;

	static CrapsDriver sharedApp = null;

	private int INITIAL_MONEY = 100,
				money,
				dice[];
	private boolean playing = true;
	private int betAmount = 5, hop1 = 0, hop2 = 0;
	private Vector<Integer> winning, losing;

	private final Semaphore shoot = new Semaphore(0);
	private JFrame theFrame;
	private JSpinner spinner;
	private JButton shootButton;
	private Timer flashTimer, autoplayTimer;
	private int flash = 0;
	private CrapsGame game;
	private BufferedImage crapsTable, chip, onButton, offButton;
	private Font font = new Font(Font.SANS_SERIF, Font.BOLD, 16);
	private FontMetrics metrics = null;

	private static final Field[] fields = 
		{new Field(new Rectangle(48, 361, 544, 44), CrapsGame.BET_PASS),
		 new Field(new Rectangle(157, 312, 435, 45), CrapsGame.BET_DONT_PASS),
		 new Field(new Rectangle(128, 160, 464, 83), CrapsGame.BET_COME),
		 new Field(new Rectangle(128, 32, 77, 123), CrapsGame.BET_DONT_COME),
		 new Field(new Rectangle(811, 328, 27, 52), CrapsGame.SNAKE_EYES),
		 new Field(new Rectangle(720, 328, 30, 52), CrapsGame.ACE_DEUCE),
		 new Field(new Rectangle(783, 383, 93, 52), CrapsGame.YO),
		 new Field(new Rectangle(913, 328, 27, 52), CrapsGame.BOXCARS),
		 new Field(new Rectangle(865, 328, 20, 52), CrapsGame.HI_LO),
		 new Field(new Rectangle(690, 438, 278, 28), CrapsGame.THREE_WAY),
		 new Field(new Rectangle(654, 252, 27, 214), CrapsGame.CE_CRAPS, 2),
		 new Field(new Rectangle(624, 244, 27, 230), CrapsGame.CE_YO, 2),
		 new Field(new Rectangle(688, 188, 185, 28), CrapsGame.SEVEN),
		 new Field(new Rectangle(784, 328, 27, 52), CrapsGame.HORN_2, 4),
		 new Field(new Rectangle(690, 328, 30, 52), CrapsGame.HORN_3, 4),
		 new Field(new Rectangle(690, 383, 93, 52), CrapsGame.HORN_11, 4),
		 new Field(new Rectangle(886, 328, 27, 52), CrapsGame.HORN_12, 4),
		 new Field(new Rectangle(839, 328, 27, 52), CrapsGame.WHIRL_2, 5),
		 new Field(new Rectangle(750, 328, 30, 52), CrapsGame.WHIRL_3, 5),
		 new Field(new Rectangle(874, 188, 93, 28), CrapsGame.WHIRL_7, 5),
		 new Field(new Rectangle(876, 383, 93, 52), CrapsGame.WHIRL_11, 5),
		 new Field(new Rectangle(940, 328, 27, 52), CrapsGame.WHIRL_12, 5),
		 new Field(new Rectangle(580, 438, 30, 30), CrapsGame.ON_THE_HOP),
		 new Field(new Rectangle(127, 237, 466, 74), CrapsGame.FIELD),
		 new Field(new Rectangle(830, 274, 137, 52), CrapsGame.HARD_4),
		 new Field(new Rectangle(689, 218, 137, 52), CrapsGame.HARD_6),
		 new Field(new Rectangle(690, 274, 137, 52), CrapsGame.HARD_8),
		 new Field(new Rectangle(830, 218, 137, 52), CrapsGame.HARD_10),
		 new Field(new Rectangle(205, 71, 77, 83), CrapsGame.PLACE_4),
		 new Field(new Rectangle(284, 71, 77, 83), CrapsGame.PLACE_5),
		 new Field(new Rectangle(362, 71, 77, 83), CrapsGame.PLACE_6),
		 new Field(new Rectangle(440, 71, 77, 83), CrapsGame.PLACE_8),
		 new Field(new Rectangle(518, 71, 77, 83), CrapsGame.PLACE_9),
		 new Field(new Rectangle(596, 71, 77, 83), CrapsGame.PLACE_10)};
									  
	private GraphicalCraps (CrapsGame game) throws IOException {
		money = INITIAL_MONEY;
		setupTable(game);
		
		theFrame = new JFrame("Craps");
		theFrame.addWindowListener(new WinAdapter());
		theFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setupLayout();
		theFrame.pack();
		theFrame.setResizable(false);
		theFrame.setVisible(true);
		shootButton.requestFocusInWindow();		
	}
	
	static public CrapsDriver sharedInstance (CrapsGame game) throws IOException {
		if (sharedApp == null)
			sharedApp = new GraphicalCraps(game);
		return sharedApp;
	}
	
	private void setupTable (CrapsGame g) throws IOException {
		JarFile jar = new JarFile("Craps.jar");
		game = g;
		crapsTable = ImageIO.read(jar.getInputStream(jar.getEntry("table2.png")));
		chip = ImageIO.read(jar.getInputStream(jar.getEntry("chip.png")));
		onButton = ImageIO.read(jar.getInputStream(jar.getEntry("on.png")));
		offButton = ImageIO.read(jar.getInputStream(jar.getEntry("off.png")));
		jar.close();
		
		// create and set the click area for the pass line
		
		Area passLine, passLeft, passJoint, passMask;
		Ellipse2D joint = new Ellipse2D.Double();
		Rectangle2D line = new Rectangle2D.Double();
		
		joint.setFrame(32, 256, 150, 150);
		passJoint = new Area(joint);
		joint.setFrame(77, 211, 150, 150);
		passMask = new Area(joint);
		passJoint.subtract(passMask);
		
		line.setFrame(33, 96, 45, 225);
		passLeft = new Area(line);
		line.setFrame(120, 361, 474, 45);
		passLine = new Area(line);
		passLine.add(passLeft);
		passLine.add(passJoint);
		
		fields[CrapsGame.BET_PASS].setClickArea(passLine);
		
		// create and set the click area for the don't pass line

		Area dpLine, dpLeft, dpJoint, dpMask;
		Rectangle2D dontPass = new Rectangle2D.Double();
		
		dontPass.setFrame(81, 63, 45, 235);
		dpLeft = new Area(dontPass);
		dontPass.setFrame(140, 313, 454, 45);
		dpLine = new Area(dontPass);
		dpLine.add(dpLeft);
		
		joint.setFrame(79, 259, 100, 100);
		dpJoint = new Area(joint);
		joint.setFrame(126, 211, 100, 100);
		dpMask = new Area(joint);
		dpJoint.subtract(dpMask);
		dpLine.add(dpJoint);
		
		fields[CrapsGame.BET_DONT_PASS].setClickArea(dpLine);
	}
	
	private void setupLayout() {
		JPanel table = new JPanel(),
			   widgetPanel = new JPanel();
		table.addMouseListener(new Adapter());
		table.add(this);

		Dimension puzzleDrawingSize = new Dimension(1000, 500);
		setMinimumSize(puzzleDrawingSize);
		setPreferredSize(puzzleDrawingSize);
		setMaximumSize(puzzleDrawingSize);

		theFrame.getContentPane().add(table, BorderLayout.PAGE_START);
		theFrame.getContentPane().setBackground(new Color(0, 0x99, 0x33));
		table.setBackground(new Color(0, 0x99, 0x33));
		widgetPanel.setBackground(new Color(0, 0x99, 0x33));
		
		JButton newGame = new JButton("New game");
		newGame.addActionListener(this);
		widgetPanel.add(newGame);
		newGame.setAlignmentX(CENTER_ALIGNMENT);
		
		shootButton = new JButton("Shoot");
		shootButton.addActionListener(this);
		widgetPanel.add(shootButton);
		shootButton.setAlignmentX(CENTER_ALIGNMENT);
		
		spinner = new JSpinner(new SpinnerNumberModel(5, 0, 1000000, 1));
		spinner.addChangeListener(this);
		widgetPanel.add(spinner);
		
		JButton hop = new JButton("On the hop...");
		hop.addActionListener(this);
		widgetPanel.add(hop);
		hop.setAlignmentX(CENTER_ALIGNMENT);
		
		widgetPanel.setBorder(new javax.swing.border.EmptyBorder(20, 0, 30, 0));		
		theFrame.getContentPane().add(widgetPanel, BorderLayout.PAGE_END);
		
		flashTimer = new Timer(250, this);
		flashTimer.setInitialDelay(250);
		
		autoplayTimer = new Timer(1000, this);
		//autoplayTimer.start();
	}

	////////////////////////////////////
	
	class Adapter extends MouseAdapter {
		boolean validBet (int bet) {
			if (money < betAmount) return false;

			if (bet == CrapsGame.BET_PASS)
				return game.getBet(CrapsGame.BET_DONT_PASS) == 0 && (game.getBet(CrapsGame.BET_PASS) > 0 || game.getPoint() == 0);
			else if (bet == CrapsGame.BET_DONT_PASS)
				return game.getBet(CrapsGame.BET_PASS) == 0 && (game.getBet(CrapsGame.BET_DONT_PASS) > 0 || game.getPoint() == 0);
			else if (bet == CrapsGame.BET_DONT_COME)
				return game.getPoint() > 0 && (game.getBet(CrapsGame.BET_COME) == 0 || game.getComePoint() == 0);
			else if (bet == CrapsGame.BET_COME)
				return game.getPoint() > 0 && game.getBet(CrapsGame.BET_DONT_COME) == 0;
			
			return true;
		}
		
		void collectBet (int bet, int split, int amt, int odds, boolean takeAll) {
			game.collect(bet);
			if (!takeAll) game.placeBet(bet, betAmount);
		}
		
		void collectFrom (int i, int bet, int amt, int odds, boolean takeAll) {
			int split = fields[i].getSplit();
			if (takeAll) {
				collectBet(bet, split, amt, odds, true);
				if (bet == CrapsGame.ON_THE_HOP)
					fields[CrapsGame.ON_THE_HOP].setClickable(false);
			} else
				collectBet(bet, split, amt, odds, false);
		}
		
		void placeBet (int button, int i, int bet, int amt) {
			if (button == MouseEvent.BUTTON1)
				game.placeBet(bet, betAmount);
			else if (button == MouseEvent.BUTTON3 && amt > 0 &&
					  (i <= CrapsGame.BET_DONT_COME || i >= CrapsGame.BET_MULTI)) {
				game.placeOdds(bet, betAmount);
			}
		}
		
		public void mouseClicked (MouseEvent e) {
			int x = e.getX(), y = e.getY();

			stopFlashing();
			
			for (int i = 0; i < fields.length; i++) {
				int clicks = e.getClickCount(),
					button = e.getButton(),
					bet = fields[i].getBet(),
					amt = game.getBet(bet),
					odds = game.getOdds(bet);

				if (fields[i].contains(x, y)) {
					if (clicks == 1 && validBet(fields[i].getBet()))
						placeBet(button, i, bet, amt);
					else if (clicks == 2 && button == MouseEvent.BUTTON1)
						collectFrom(i, bet, amt, odds, false);
					else if (clicks == 3 && button == MouseEvent.BUTTON1)
						collectFrom(i, bet, amt, odds, true);
					
					repaint();
				}
			}
			
			shootButton.requestFocusInWindow();
		}		
	}
	
	class WinAdapter extends WindowAdapter {
		public void windowClosing (WindowEvent e) {
			String msg;
			int net = money + game.getMoneyOnTable() - INITIAL_MONEY;

			theFrame.setVisible(false);
			if (net > 0)
				msg = "You made $" + net + ". Way to go, champ!";
			else if (net < 0)
				msg = "You lost $" + -net + ". Way to go, champ!";
			else
				msg = "You broke even.";
			
			JOptionPane.showMessageDialog(null, msg);
			System.exit(0);
		}
	}
	
	////////////////////////////////////

	public void stateChanged (ChangeEvent e) {
		SpinnerModel model = spinner.getModel();
		if (model instanceof SpinnerNumberModel) {
			betAmount = (Integer) model.getValue();
		}
	}
	
	void hopBet () {
		while (true) {
			String answer = JOptionPane.showInputDialog("On which dice?");
			if (answer == null)
				return;

			StringTokenizer st = new StringTokenizer(answer);
			try {
				int die1 = Integer.parseInt(st.nextToken()),
					die2 = Integer.parseInt(st.nextToken());
					if (die1 >= 1 && die1 <= 6 &&
						die2 >= 1 && die2 <= 6) {
						hop1 = die1;
						hop2 = die2;
						game.placeHop(die1, die2, betAmount);
						fields[CrapsGame.ON_THE_HOP].setClickable(true);
						return;
					}				
			} catch (NoSuchElementException e) { }
		}
	}
	
	public int newGame() {
		int start = 0;
		
		while (start < 1) {
			String in = JOptionPane.showInputDialog("How much money to start?", INITIAL_MONEY);
			try {
				start = Integer.parseInt(in);
				if (start == 0)
					return 0;
			} catch (Exception e) {
				return 0;
			}
		}
		
		INITIAL_MONEY = start;
		game.reset();
		
		return start;
	}

	void stopFlashing () {
		flash = 0;
		flashTimer.stop();
		winning = losing = null;
		for (int i = 0; i < fields.length; i++)
			fields[i].setFlash(false);
		repaint();
	}
	
	public void actionPerformed(ActionEvent se) {
		String command = se.getActionCommand();
		if (se.getSource() == flashTimer) {
			if (--flash == 0)
				stopFlashing();
			else if (flash < 8)
				flashTimer.setDelay(50);
			repaint();
		} else if (se.getSource() == autoplayTimer)
			shoot.release();
		else if (command.equals("Shoot"))
			shoot.release();
		else if (command.equals("New game"))
			newGame();
		else if (command.equals("On the hop..."))
			hopBet();

		repaint();
	}
	
	////////////////////////////////////

	void drawButton (Graphics g) {
		int point = game.getPoint();
		if (point == 0)
			g.drawImage(offButton, 10, 10, null);
		else {
			int n = (point - 4 - (point > 7 ? 1 : 0)) + 1;
			g.drawImage(onButton, 142 + (78 * n), 27, null);
		}		
	}
	
	void drawChip (Graphics g, int where, boolean flash) {
		String bet = String.valueOf(game.getBet(fields[where].getBet(), true)),
			   odds = String.valueOf(game.getOdds(fields[where].getBet()));
		int x, y, betWidth = metrics.stringWidth(bet), oddsWidth = metrics.stringWidth(odds), h = metrics.getHeight();

		int place;
		switch (game.getComePoint()) {
			case 4: place = CrapsGame.PLACE_4; break;
			case 5: place = CrapsGame.PLACE_5; break;
			case 6: place = CrapsGame.PLACE_6; break;
			case 8: place = CrapsGame.PLACE_8; break;
			case 9: place = CrapsGame.PLACE_9; break;
			case 10: place = CrapsGame.PLACE_10; break;
			default: place = where;
		}

		if (place != where && where == CrapsGame.BET_COME || where == CrapsGame.BET_DONT_COME) {
			x = fields[place].getDisplayArea().x - 15 + fields[place].getDisplayArea().width / 2;
			y = fields[place].getDisplayArea().y + 25 + fields[place].getDisplayArea().height / 2;
		} else {
			x = fields[where].getDisplayArea().x - 15 + fields[where].getDisplayArea().width / 2;
			y = fields[where].getDisplayArea().y - 15 + fields[where].getDisplayArea().height / 2;
		}

		if (!flash) {
			g.setColor(Color.BLACK);
			g.drawImage(chip, x, y, null);
			g.drawString(bet, x + 15 - betWidth / 2, y + 10 + h / 2);
			if (game.getOdds(fields[where].getBet()) > 0) {
				g.drawImage(chip, x, y + 35, null);
				g.drawString(odds, x + 15 - oddsWidth / 2, y + 45 + h / 2);
			}			
		}
	}
	
	/**
	 * draw a dot centered at (x, y) with radius 5
	 * @param g Graphics context
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	public void drawDot (Graphics g, int x, int y) {
		((Graphics2D)g).fill(new Ellipse2D.Double(x - 5, y - 5, 10, 10));
	}

	public void drawDie (Graphics g, int x, int y, int num) {
		if (num == 0) return;
		g.setColor(Color.RED);
		((Graphics2D)g).fill(new Rectangle(x, y, 50, 50));
		g.setColor(Color.WHITE);
		switch (num) {
			case 1:
				drawDot(g, x + 25, y + 25);
				break;

			case 2:
				drawDot(g, x + 10, y + 40);
				drawDot(g, x + 40, y + 10);
				break;

			case 3:
				drawDot(g, x + 10, y + 40);
				drawDot(g, x + 25, y + 25);
				drawDot(g, x + 40, y + 10);
				break;

			case 4:
				drawDot(g, x + 10, y + 10);
				drawDot(g, x + 10, y + 40);
				drawDot(g, x + 40, y + 10);
				drawDot(g, x + 40, y + 40);
				break;

			case 5:
				drawDot(g, x + 10, y + 10);
				drawDot(g, x + 10, y + 40);
				drawDot(g, x + 25, y + 25);
				drawDot(g, x + 40, y + 10);
				drawDot(g, x + 40, y + 40);
				break;
				
			case 6:
				drawDot(g, x + 10, y + 10);
				drawDot(g, x + 10, y + 40);
				drawDot(g, x + 10, y + 25);
				drawDot(g, x + 40, y + 25);
				drawDot(g, x + 40, y + 10);
				drawDot(g, x + 40, y + 40);
		}
	}

	////////////////////////////////////

	void announceRoll (Graphics g) {
		//int comePoint = game.getComePoint();
		
		g.setFont(font);
		g.setColor(Color.WHITE);
		g.drawString("$" + money, 750, 80);
		//if (comePoint > 0) g.drawString("Come point is " + comePoint, 750, 80 + metrics.getHeight());

		if (dice != null) {
			drawDie(g, 750, 60 + metrics.getHeight() * 2, dice[0]);
			drawDie(g, 815, 60 + metrics.getHeight() * 2, dice[1]);
		}
	}
	
	public void paint (Graphics g) {
		if (metrics == null) {
			metrics = g.getFontMetrics(font);
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}

		g.drawImage(crapsTable, 0, 0, null);
		drawButton(g);
		announceRoll(g);
		if (game.getBet(CrapsGame.ON_THE_HOP) > 0) { 
			drawDie(g, 450, 438, hop1);
			drawDie(g, 510, 438, hop2);
		}
		
		if (winning != null)
			for (int i : winning)
				fields[i].flash();

		for (int i = 0; i < fields.length; i++)
			if (game.getBet(fields[i].getBet(), true) > 0 && !fields[i].isFlashing())
				drawChip(g, i, false);

		if (losing != null)
			for (int i : losing)
				drawChip(g, i, fields[i].flash());
	}
	
	void flashChips () {
		winning = game.getWinningBets();
		losing = game.getLosingBets();
		if (winning.size() + losing.size() > 0) {
			flash = 10;
			flashTimer.setDelay(250);
			flashTimer.start();
		}
	}
	
	////////////////////////////////////
	
	public void setDice (int[] d) {
		dice = d;
	}

	public void setPlaying (boolean b) {
		playing = b;
	}
	
	public boolean isPlaying () {
		return playing;
	}
	
	public void setMoney (int amount) {
		money = amount;
	}
	
	public void addMoney (int amount) {
		money += amount;
	}
	
	public int getMoney () {
		return money;
	}
	
	public int getInitialMoney () {
		return INITIAL_MONEY;
	}
	
	void think (int when) {
		if (money <= 0) {
			//autoplayTimer.stop();
			return;
		}
		int min = 50;
		switch (when) {
			case 1:				
				if (game.getBet(CrapsGame.BET_PASS) == 0)
					game.placeBet(CrapsGame.BET_PASS, betAmount);
				if (game.getBet(CrapsGame.BET_PASS) > 0 &&
					game.getOdds(CrapsGame.BET_PASS) == 0)
					game.placeOdds(CrapsGame.BET_PASS, betAmount);
				if (game.getBet(CrapsGame.HARD_10) == 0 && money > min) {
					game.placeBet(CrapsGame.HARD_10, betAmount);
					min += 50;
				} else if (game.getBet(CrapsGame.PLACE_8) == 0)
					game.placeBet(CrapsGame.PLACE_8, betAmount);
				if (game.getBet(CrapsGame.PLACE_10) == 0 && money > min) {
					game.placeBet(CrapsGame.PLACE_10, betAmount);
					min += 50;
				}
				if (game.getBet(CrapsGame.HARD_6) == 0 && money > min)
					game.placeBet(CrapsGame.HARD_6, betAmount);
				if (game.getBet(CrapsGame.PLACE_6) == 0 && money > min)
					game.placeBet(CrapsGame.PLACE_6, betAmount);
				break;
			
			case 2:
				collectAll();
				/*if (money >= 1000) {
					betAmount = 100;
					spinner.setValue(100);
				} else if (money >= 500) {
					betAmount = 50;
					spinner.setValue(100);
				} else if (money >= 100) {
					betAmount = 10;
					spinner.setValue(10);
				} else*/
				if (money < 100)
					betAmount = 5;
				else
					betAmount = (int) Math.pow(10, Math.log10(money) - 1);
				spinner.setValue(betAmount);
				break;
		}
	}
	
	public void beginTurn () {
		if (game.getBet(CrapsGame.ON_THE_HOP) == 0)
			fields[CrapsGame.ON_THE_HOP].setClickable(false);
		try { shoot.acquire(); }
		catch (InterruptedException e) { }
		//think(1);
	}
	
	void collectAll () {
		Vector<Integer> winning, losing;
		winning = game.getWinningBets();
		losing = game.getLosingBets();
		for (int i : winning) {
			game.collect(i);
			game.placeBet(i, betAmount);
		}
		for (int i : losing)
			game.placeBet(i, betAmount);
	}
	
	public void endTurn () {
		repaint();
		flashChips();
		//think(2);
		/*if (money <= 0 && game.getMoneyOnTable() == 0) {
			JOptionPane.showMessageDialog(null, "Get outta here, ya bum!");
		}*/
	}
	
	public void play () {
	}
	
	public static void main (String args[]) {
		CrapsGame game = new CrapsGame();
		CrapsDriver con = null;
		try {
			con = sharedInstance(game);
			game.setDriver(con);
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
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
