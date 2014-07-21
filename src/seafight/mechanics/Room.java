package seafight.mechanics;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import seafight.communication.ClientState;
import seafight.communication.vo.ClientStateVO;
import seafight.communication.vo.IValueObject;
import seafight.communication.vo.ShipDeadVO;
import seafight.communication.vo.ShotResponseVO;
import seafight.communication.vo.SystemMessageVO;
import seafight.communication.vo.TurnStepVO;
import seafight.mechanics.combat.CellType;
import seafight.mechanics.combat.CombatMap;
import seafight.mechanics.combat.Position;
import seafight.mechanics.combat.Ship;

public class Room {
	private static final long REQUEST_EXPIRE_TIME_INTERVAL = 1 * 60 * 1000;
	private static final long BEFORE_COMBAT_EXPIRE_TIME_INTERVAL = 5 * 60 * 1000;
	
	private static final int MAX_FIGHTERS = 2;
	
	private static AtomicInteger idCreator = new AtomicInteger();
	
	private int id;
	private int state;
	private long expireTime;
	
	private List<IFighter> fighters = new LinkedList<IFighter>();
	private IFighter activeFighter;
	private IFighter waitFighter;
	
	public static Room create() { return new Room(); }
	
	private Room() {
		this.id = idCreator.incrementAndGet();
		this.state = RoomState.REQUEST;
		this.setExpireTime(REQUEST_EXPIRE_TIME_INTERVAL);
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getState() {
		return this.state;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	public long getExpireTime() {
		return this.expireTime;
	}
	
	public void setExpireTime(long interval) {
		this.expireTime = System.currentTimeMillis() + interval;
	}
	
	public boolean isWaitEnemy() {
		return this.fighters.size() != MAX_FIGHTERS;
	}
	
	public void addFighter(IFighter fighter) {
		fighter.setCombatContext(new CombatContext(this, new CombatMap()));
		
		synchronized(this.fighters) {
			this.fighters.add(fighter);	
		}
		
		if(this.fighters.size() == MAX_FIGHTERS) {
			this.activeFighter = fighter;
			
			this.setState(RoomState.BEFORE_COMBAT);
			this.setExpireTime(BEFORE_COMBAT_EXPIRE_TIME_INTERVAL);
			
			this.notifyFighters(new ClientStateVO(ClientState.BEFORE_COMBAT));
		} else {
			this.waitFighter = fighter;
			this.waitFighter.setClientState(ClientState.FIND_ENEMY);
			this.notifyFighter(this.waitFighter, new ClientStateVO(ClientState.FIND_ENEMY));
		}
	}
	
	public void removeFighter(IFighter fighter) {
		fighter.setCombatContext(null);
		
		synchronized(this.fighters) {
			this.fighters.remove(fighter);
		}
	
		this.destroyCombat(SystemMessage.ENEMY_DESTROY);	
	}
	
	public void turnFighters() {
		IFighter temp = this.activeFighter;
		this.activeFighter = this.waitFighter;
		this.waitFighter = temp;
		
		this.activeFighter.getCommunicationContext().sendData(new TurnStepVO());
		
		this.setState(RoomState.COMBAT);
	}
	
	public boolean isReadyToCombat() {
		return (this.waitFighter.getCombatContext().getCombatMap().isReady() && this.activeFighter.getCombatContext().getCombatMap().isReady());
	}
	
	public boolean isFinal() {
		return (!this.activeFighter.getCombatContext().getCombatMap().isAlive() || !this.waitFighter.getCombatContext().getCombatMap().isAlive());
	}
	
	public void startCombat() {
		this.setState(RoomState.COMBAT);
		
		this.notifyFighters(new ClientStateVO(ClientState.COMBAT));
		this.turnFighters();		
	}
	
	public void destroyCombat(int reasonCode) {
		this.setState(RoomState.DESTROY);
		
		this.clearCombat();
		this.notifyFighters(new SystemMessageVO(reasonCode));	
	}
	
	public void finalCombat() {
		int active = (this.activeFighter.getCombatContext().getCombatMap().isAlive()) ? SystemMessage.COMBAT_WIN : SystemMessage.COMBAT_LOSS;
		int wait = (this.waitFighter.getCombatContext().getCombatMap().isAlive()) ? SystemMessage.COMBAT_WIN : SystemMessage.COMBAT_LOSS;
				
		this.notifyFighter(this.activeFighter, new SystemMessageVO(active));
		this.notifyFighter(this.waitFighter, new SystemMessageVO(wait));
		
		this.clearCombat();
		
		this.setState(RoomState.DESTROY);
	}
	
	public void shot(IFighter fighter, int positionX, int positionY) {
		if(fighter != this.activeFighter)
			return;
		
		CombatMap combatMap = this.waitFighter.getCombatContext().getCombatMap();
		Position position = new Position(positionX, positionY);
		int cellType = -1;
		
		if(combatMap.getCellByPosition(position).getType() == CellType.EMPTY) {
			combatMap.getCellByPosition(position).setType(CellType.HIT);
			
			cellType = CellType.HIT;
		}		
		else if(combatMap.getCellByPosition(position).getType() == CellType.SHIP) {
			combatMap.getCellByPosition(position).setType(CellType.DEAD);
			
			cellType = CellType.DEAD;
		}
		
		this.notifyFighters(new ShotResponseVO(positionX, positionY, cellType));
		
		if(cellType == CellType.DEAD) {
			Ship ship = combatMap.getShipByPosition(position);
			ship.hit();
			
			if(!ship.isAlive())
				this.notifyFighters(new ShipDeadVO(ship.getPositionX(), ship.getPositionY(), ship.getSize(), ship.getOrientation()));
		}
		
		if(this.isFinal()) { this.setState(RoomState.AFTER_COMBAT); }
		else { this.setState(RoomState.TURN_FIGHTER); }			
	}
	
	private void notifyFighter(IFighter fighter, IValueObject vo) {
		fighter.getCommunicationContext().sendData(vo);
	}
	
	private void notifyFighters(IValueObject vo) {
		synchronized(this.fighters) {
			for(IFighter fighter : this.fighters)
				this.notifyFighter(fighter, vo);				
		}
	}
	
	private void clearCombat() {
		synchronized(this.fighters) {
			for(IFighter fighter : this.fighters)  {
				fighter.setClientState(ClientState.CONNECTED);				
				fighter.setCombatContext(null);							
			}
		}
	}
}