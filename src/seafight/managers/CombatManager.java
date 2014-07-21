package seafight.managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.apache.log4j.Logger;

import seafight.communication.ThreadClient;
import seafight.communication.contexts.NetworkContext;
import seafight.mechanics.IFighter;
import seafight.mechanics.Room;
import seafight.mechanics.RoomState;
import seafight.mechanics.SystemMessage;

public class CombatManager extends Thread {
	private static final Logger logger = Logger.getLogger(CombatManager.class);
	
	private static volatile CombatManager instance = null;
	
	public synchronized static CombatManager getInstance() {
		if(instance == null)
			instance = new CombatManager();
		
		return instance;
	}
	
	private Queue<IFighter> fighters = new ConcurrentLinkedDeque<IFighter>();
	
	private List<Room> rooms = new ArrayList<Room>();
	
	private CombatManager() { }
	
	public void addFighterToQueue(IFighter fighter) {
		this.fighters.add(fighter);
	}
	
	@Override
	public void run() {
		while(true) {
			try { Thread.sleep(100);	}
			catch (InterruptedException e) { logger.error(e); }
			
			long currentTime = System.currentTimeMillis();
			
			Iterator<Room> iterator = this.rooms.iterator();
			
			while(iterator.hasNext()) {
				Room room = iterator.next();
				
				if(room.getState() == RoomState.REQUEST /*&& room.getExpireTime() <= currentTime*/) {
					ThreadClient bot = new ThreadClient();
					new Thread(bot).start();
					room.addFighter(bot);
					
					logger.debug("Bot was joined room ID[" + room.getId() + "]");
					continue;
				}
				
				if(room.getState() == RoomState.BEFORE_COMBAT && room.isReadyToCombat()) {
					room.startCombat();
					
					continue;
				}
				
				if(room.getState() == RoomState.BEFORE_COMBAT && room.getExpireTime() <= currentTime) {
					room.destroyCombat(SystemMessage.WAIT_ENEMY_EXPIRE);
					
					continue;
				}
				
				if(room.getState() == RoomState.AFTER_COMBAT) {
					room.finalCombat();
					
					continue;					
				}
				
				if(room.getState() == RoomState.TURN_FIGHTER) {					
					room.turnFighters();
					
					continue;
				}
				
				if(room.getState() == RoomState.DESTROY) {					
					logger.debug("Room ID[" + room.getId() + "] was destroyed.");
					iterator.remove();
					
					continue;
				}				
			}
			
			while(!this.fighters.isEmpty()) {
				IFighter fighter = this.fighters.poll();				
				boolean complete = false;
				
				if(!this.rooms.isEmpty()) {
					for(Room room : this.rooms) {
						if(room.getState() == RoomState.REQUEST && room.isWaitEnemy()) {
							room.addFighter(fighter);
							
							complete = true;
							
							logger.debug("Client [" + ((NetworkContext)fighter.getCommunicationContext()).getHostAddress() + ":" + ((NetworkContext)fighter.getCommunicationContext()).getPort() + "] was joined room ID[" + room.getId() + "]");
							break;
						}
					}						
				}

				if(this.rooms.isEmpty() || !complete) {
					Room room = Room.create();
					room.addFighter(fighter);
					this.rooms.add(room);
					
					logger.debug("Client [" + ((NetworkContext)fighter.getCommunicationContext()).getHostAddress() + ":" + ((NetworkContext)fighter.getCommunicationContext()).getPort() + "] create new room ID[" + room.getId() + "]");
				}
			}			
		}
	}
}