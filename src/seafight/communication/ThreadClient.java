package seafight.communication;

import org.apache.log4j.Logger;

import seafight.communication.contexts.ThreadContext;
import seafight.communication.local.Message;
import seafight.mechanics.CombatContext;
import seafight.mechanics.IFighter;

public class ThreadClient extends AbstractClient implements IFighter, Runnable {
	private static final Logger logger = Logger.getLogger(ThreadClient.class);
	
	private CombatContext combatContext;
	
	private boolean shutdown = false;
	
	public ThreadClient() {
		this.communicationContext = new ThreadContext();
		this.clientState = ClientState.CONNECTED;		
	}
	
	public ThreadContext getThreadContext() {
		return (ThreadContext) this.communicationContext;
	}
	
	public boolean isShutdown() { return this.shutdown; }
	
	public void shutdown() { this.shutdown = true; }
	
	@Override
	public void onDisconnect() { }
	
	@Override
	public void run() {
		while(!this.isShutdown()) {
			try { Thread.sleep(500);	}
			catch (InterruptedException e) { logger.error(e); }
			
			try {
				Message message = this.getThreadContext().getQueue().take();
				message.execute(this);
			} catch (InterruptedException e) { logger.error(e); }						
		}
		
		this.onDisconnect();
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