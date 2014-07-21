package seafight.communication.vo;

public class SystemMessageVO implements IValueObject {
	private int messageId;
	
	public SystemMessageVO(int messageId) {
		this.messageId = messageId;
	}
	
	public int getMessageId() { return this.messageId; }
	
	@Override
	public int getValueObjectID() { return 0x7D; }
}