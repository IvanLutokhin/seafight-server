package seafight.communication.local.messages;

import seafight.communication.ThreadClient;
import seafight.communication.local.Message;
import seafight.communication.vo.ShipDeadVO;
import seafight.helpers.MapHelper;
import seafight.mechanics.combat.Position;
import seafight.mechanics.combat.ShipEnvironment;

public class ShipDeadMessage extends Message {
	@Override
	public void execute(ThreadClient client) {
		ShipDeadVO sdvo = (ShipDeadVO) this.vo;
		
		if(!client.getThreadContext().isShipDead()) {			
			ShipEnvironment shipEnvironment = MapHelper.getShipEnvironment(new Position(sdvo.getPositionX(), sdvo.getPositionY()), sdvo.getSize(), sdvo.getOrientation());
			client.getThreadContext().removeShipEnvironmentPositions(shipEnvironment);
		}
	}
}