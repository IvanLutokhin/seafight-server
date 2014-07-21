package seafight.communication.local.messages;

import seafight.communication.ThreadClient;
import seafight.communication.local.Message;

public class SystemMessageMessage extends Message {
	@Override
	public void execute(ThreadClient client) {
		client.shutdown();
	}
}