package seafight.communication.contexts;

import seafight.communication.vo.IValueObject;

public interface ICommunicationContext {
	void sendData(IValueObject vo);
}