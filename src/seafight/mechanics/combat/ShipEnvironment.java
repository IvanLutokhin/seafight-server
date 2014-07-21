package seafight.mechanics.combat;

public class ShipEnvironment {
	private int x;
	private int y;
	private int w;
	private int h;
	
	public ShipEnvironment(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public int X() { return this.x; }
	
	public int Y() { return this.y; }
	
	public int W() { return this.w; }
	
	public int H() { return this.h; }
}