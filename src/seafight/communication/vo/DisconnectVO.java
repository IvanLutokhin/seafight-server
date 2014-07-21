package seafight.communication.vo;

public class DisconnectVO implements IValueObject {
	private int reasonCode;
	
	public DisconnectVO(int reasonCode) {
		this.reasonCode = reasonCode;		
	}
	
	public int getReasonCode() {
		return this.reasonCode;
	}
	
	@Override
	public int getValueObjectID() { return 0x7F; }
}