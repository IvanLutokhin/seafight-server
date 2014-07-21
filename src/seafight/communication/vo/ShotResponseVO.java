package seafight.communication.vo;

public class ShotResponseVO implements IValueObject {
	private int positionX;
	private int positionY;
	private int cellType;
		
	public ShotResponseVO(int positionX, int positionY, int cellType) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.cellType = cellType;		
	}

	public int getPositionX() { return this.positionX; }
	
	public int getPositionY() { return this.positionY; }
	
	public int getCellType() { return this.cellType; }
		
	@Override
	public int getValueObjectID() { return 0x02; }
}