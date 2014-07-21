package seafight.communication;

import seafight.communication.contexts.ICommunicationContext;
import seafight.communication.vo.ClientStateVO;
import seafight.communication.vo.IValueObject;

public abstract class AbstractClient {
	protected int clientState;
	
	protected ICommunicationContext communicationContext;
	
	public int getClientState() {
		return this.clientState;
	}
	
	public void setClientState(int clientState) {
		this.clientState = clientState;
	}
		
	public void changeClientState(int clientState) {
		this.clientState = clientState;
		
		this.sendData(new ClientStateVO(this.clientState));
	}
	
	public ICommunicationContext getCommunicationContext() {
		return this.communicationContext;
	}
	
	public void sendData(IValueObject vo) {
		this.communicationContext.sendData(vo);
	}
	
	public abstract void onDisconnect();
}