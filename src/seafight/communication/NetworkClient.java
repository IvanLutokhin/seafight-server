package seafight.communication;

import seafight.communication.contexts.NetworkContext;
import seafight.mechanics.CombatContext;
import seafight.mechanics.IFighter;

public class NetworkClient extends AbstractClient implements IFighter {
	private CombatContext combatContext;
	
	public NetworkClient(NetworkContext networkContext) {
		this.communicationContext = networkContext;
		this.clientState = ClientState.CONNECTED;
	}
	
	public NetworkContext getNetworkContext() {
		return (NetworkContext) this.communicationContext;
	}

	@Override
	public void onDisconnect() {
		if(this.combatContext != null) {
			this.combatContext.getRoom().removeFighter(this);
		}
	}

	@Override
	public CombatContext getCombatContext() {
		return this.combatContext;
	}

	@Override
	public void setCombatContext(CombatContext combatContext) {
		this.combatContext = combatContext;		
	}	
}