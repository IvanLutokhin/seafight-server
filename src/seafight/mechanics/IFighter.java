package seafight.mechanics;

import seafight.communication.contexts.ICommunicationContext;

public interface IFighter {
	int getClientState();
	
	void setClientState(int clientState);
		
	void changeClientState(int clientState);
	
	CombatContext getCombatContext();
	
	void setCombatContext(CombatContext combatContext);
	
	ICommunicationContext getCommunicationContext();
}