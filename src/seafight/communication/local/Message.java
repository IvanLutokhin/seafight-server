package seafight.communication.local;

import seafight.communication.ThreadClient;
import seafight.communication.vo.IValueObject;

public abstract class Message {
	protected IValueObject vo;
	
	public void setValueObject(IValueObject vo) { this.vo = vo;	}
	
	public IValueObject getValueObject() { return this.vo; }
	
	public abstract void execute(ThreadClient client);
}