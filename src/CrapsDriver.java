public interface CrapsDriver {
	static CrapsDriver sharedApp = null;
	int INITIAL_MONEY = 0,
		money = 0,
		dice[] = null;
	boolean playing = false;

	public void setDice (int dice[]);
	public boolean isPlaying ();
	public void setPlaying (boolean playing);
	public void setMoney (int amount);
	public void addMoney (int amount);
	public int getMoney ();
	public int getInitialMoney ();
	public void beginTurn ();
	public void play ();
	public void endTurn ();
}
