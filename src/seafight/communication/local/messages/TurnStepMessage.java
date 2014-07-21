package seafight.communication.local.messages;

import seafight.communication.ThreadClient;
import seafight.communication.local.Message;
import seafight.mechanics.combat.Position;

public class TurnStepMessage extends Message {
	@Override
	public void execute(ThreadClient client) {
		int k = (int) (Math.random() * client.getThreadContext().getPositions().size());
		
		Position position = client.getThreadContext().getPositions().get(k);
		
		client.getThreadContext().setShotResponse(true);
		client.getThreadContext().setShipDead(true);
		
		client.getCombatContext().getRoom().shot(client, position.X(), position.Y());
	}
}