package seafight.communication.vo;

public class ShipDeadVO implements IValueObject {
	private int positionX;
	private int positionY;
	private int size;
	private int orientation;
		
	public ShipDeadVO(int positionX, int positionY, int size, int orientation) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.size = size;
		this.orientation = orientation;
	}

	public int getPositionX() { return this.positionX; }
	
	public int getPositionY() { return this.positionY; }
	
	public int getSize() { return this.size; }
	
	public int getOrientation() { return this.orientation; }
	
	@Override
	public int getValueObjectID() { return 0x03; }
}