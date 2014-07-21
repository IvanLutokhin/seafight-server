package seafight.mechanics.combat;

public class Position {
	private int x;
	private int y;
	
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int X() { return this.x; }
	
	public int Y() { return this.y; }
}