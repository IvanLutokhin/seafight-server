package seafight.mechanics.combat;

public class Cell {
	private Position position;
	private int type;
	
	public Cell(Position position, int type) {
		this.position = position;
		this.type = type;
	}
	
	public Cell(Position position) {
		this(position, CellType.EMPTY);
	}
			
	public Position getPosition() { return this.position; }
		
	public int getPositionX() { return this.position.X(); }
	
	public int getPositionY() { return this.position.Y(); }
	
	public int getType() { return this.type; }
	
	public void setType(int type) { this.type = type; }
}