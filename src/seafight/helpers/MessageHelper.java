package seafight.helpers;

import org.apache.log4j.Logger;

import seafight.communication.local.Message;
import seafight.communication.local.messages.*;
import seafight.communication.vo.IValueObject;

public class MessageHelper {
	private static final Logger logger = Logger.getLogger(MessageHelper.class);
	
	public static Message wrap(IValueObject vo) {
		Message message = null;
		
		switch(vo.getValueObjectID()) {
			case 0x01: { message = new TurnStepMessage(); break; }
			case 0x02: { message = new ShotResponseMessage(); break; }
			case 0x03: { message = new ShipDeadMessage(); break; }
			case 0x7D: { message = new SystemMessageMessage(); break; }
			case 0x7E: { message = new ClientStateMessage(); break; }
			case 0x7F: { message = new DisconnectMessage(); break; }
			default: { logger.error("Unknow ValueObject ID[" + vo.getValueObjectID() + "]"); }
		}
		
		if(message != null)
			message.setValueObject(vo);
		
		return message;
	}
}