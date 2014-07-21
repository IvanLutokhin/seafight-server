package seafight.mechanics.combat;

public class Ship {
	private Position position;	
	private int orientation;
	private int size;
	private int alive;
	
	public Ship(Position position, int size, int orientation) {
		this.position = position;
		this.size = this.alive = size;
		this.orientation = orientation;			
	}
	
	public Ship(Position position, int size) {
		this(position, size, ShipOrientation.HORIZONTAL);	
	}
		
	public Position getPosition() { return this.position; }
	
	public void setPosition(Position position) { this.position = position; }
	
	public int getPositionX() { return this.position.X(); }
	
	public int getPositionY() { return this.position.Y(); }
	
	public int getSize() { return this.size; }
	
	public void setSize(int size) { this.size = size; }
	
	public int getOrientation() { return this.orientation; }
	
	public void setOrientation(int orientation) { this.orientation = orientation; }
			
	public boolean isAlive() { return this.alive > 0; }
	
	public boolean isEquals(Position position) { return this.isEquals(position.X(), position.Y()); }
	
	public boolean isEquals(int positionX, int positionY) {
		if(this.orientation == ShipOrientation.HORIZONTAL) {
			if((this.position.Y() == positionY) && (this.position.X() <= positionX && this.position.X() + this.size > positionX))
				return true;			
		}
		else if(this.orientation == ShipOrientation.VERTICAL) {
			if((this.position.X() == positionX) && (this.position.Y() <= positionY && this.position.Y() + this.size > positionY))
				return true;
		}
		
		return false;
	}
	
	public boolean hit() {
		this.alive--;
		return this.alive == 0;
	}
}