package seafight.communication.local.messages;

import seafight.communication.ThreadClient;
import seafight.communication.local.Message;
import seafight.communication.vo.ShotResponseVO;
import seafight.mechanics.combat.Position;

public class ShotResponseMessage extends Message {
	@Override
	public void execute(ThreadClient client) {
		ShotResponseVO srvo = (ShotResponseVO) this.vo;
		
		if(client.getThreadContext().isShotResponse()) {
			for(Position p : client.getThreadContext().getPositions()) {
				if(p.X() == srvo.getPositionX() && p.Y() == srvo.getPositionY()) {
					client.getThreadContext().getPositions().remove(p);
					break;
				}
			}
			
			if(!client.getThreadContext().isShotResponse())
				client.getThreadContext().setShipDead(false);
			
			client.getThreadContext().setShotResponse(false);
		}
	}		
}