package seafight.communication.vo;

public class ClientStateVO implements IValueObject {
	private int clientState;
	
	public ClientStateVO(int clientState) {
		this.clientState = clientState;
	}
	
	public int getClientState() { return this.clientState; }
	
	@Override
	public int getValueObjectID() { return 0x7E; }
}