package seafight.communication.local.messages;

import seafight.communication.ClientState;
import seafight.communication.ThreadClient;
import seafight.communication.local.Message;
import seafight.communication.vo.ClientStateVO;
import seafight.mechanics.combat.CombatMap;

public class ClientStateMessage extends Message {	
	@Override
	public void execute(ThreadClient client) {
		ClientStateVO csvo = (ClientStateVO) this.vo;
		
		switch(csvo.getClientState()) {
			case ClientState.BEFORE_COMBAT: {
				CombatMap combatMap = client.getCombatContext().getCombatMap();				
				client.getThreadContext().autoDotShips(combatMap);
				break;
			}
			case ClientState.COMBAT: {
				client.getThreadContext().resetPositions();
				break;
			}
		}
	}
}