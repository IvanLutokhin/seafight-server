package seafight.communication.contexts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import seafight.communication.local.Message;
import seafight.communication.vo.IValueObject;
import seafight.helpers.MapHelper;
import seafight.helpers.MessageHelper;
import seafight.mechanics.combat.CombatMap;
import seafight.mechanics.combat.Position;
import seafight.mechanics.combat.Ship;
import seafight.mechanics.combat.ShipEnvironment;

public class ThreadContext implements ICommunicationContext {
	private static final int MAX_ITERATION = 50;
	
	private BlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(64);
	
	private List<Position> positions = Collections.synchronizedList(new ArrayList<Position>());
	private List<Ship> ships = new ArrayList<Ship>();
	
	private boolean shotResponse = false;
	
	private boolean shipDead = false;
	
	public ThreadContext() {
		for(int i = 0; i < CombatMap.N; i++) {
			for(int j = 0; j < CombatMap.N; j++) {				
				this.positions.add(new Position(j, i));
			}
		}
		
		for(int i = 0; i < CombatMap.SHIP_IV_COUNT; i++)
			this.ships.add(new Ship(new Position(-1, -1), 4));
		
		for(int i = 0; i < CombatMap.SHIP_III_COUNT; i++)
			this.ships.add(new Ship(new Position(-1, -1), 3));
		
		for(int i = 0; i < CombatMap.SHIP_II_COUNT; i++)
			this.ships.add(new Ship(new Position(-1, -1), 2));
		
		for(int i = 0; i < CombatMap.SHIP_I_COUNT; i++)
			this.ships.add(new Ship(new Position(-1, -1), 1));
	}
	
	public BlockingQueue<Message> getQueue() { return this.queue; }
	
	public List<Position> getPositions() { return this.positions; }
	
	public List<Ship> getShips() { return this.ships; }

	public boolean isShotResponse() { return this.shotResponse; }
	
	public void setShotResponse(boolean shotResponse) { this.shotResponse = shotResponse; }
	
	public boolean isShipDead() { return this.shipDead; }
	
	public void setShipDead(boolean shipDead) { this.shipDead = shipDead; }
	
	public void resetPositions() {
		this.positions.clear();
		
		for(int i = 0; i < CombatMap.N; i++) {
			for(int j = 0; j < CombatMap.N; j++) {				
				this.positions.add(new Position(j, i));
			}
		}	
	}
	
	public void autoDotShips(CombatMap combatMap) {
		this.resetPositions();
		
		for(int i = 0; i < this.ships.size(); i++) {
			if(!this.tryGetValidShipPosition(combatMap, this.ships.get(i))) {					
				combatMap.reset();
				i = -1;
				continue;
			}
		}	
	}
	
	private boolean tryGetValidShipPosition(CombatMap combatMap, Ship ship) {	
		boolean complete = false;
		int iteration = 0;
		
		while(!complete) {
			if(this.positions.size() == 0)
				return false;
			
			if(iteration == MAX_ITERATION) return false;
			
			int k = (int) (Math.random() * this.positions.size());
			
			Position position = this.positions.get(k);
										
			int orientation = (int) (Math.random() * 2);
			
			ship.setPosition(position);
			ship.setOrientation(orientation);
			
			if(combatMap.tryAddShip(ship)) {
				ShipEnvironment shipEnvironment = MapHelper.getShipEnvironment(ship);
				this.removeShipEnvironmentPositions(shipEnvironment);								
				
				complete = true;
			}
			
			iteration++;			
		}
		
		return true;
	}
	
	public void removeShipEnvironmentPositions(ShipEnvironment shipEnvironment) {
		for(int i = shipEnvironment.X(); i < shipEnvironment.X() + shipEnvironment.W(); i++) {
			for(int j = shipEnvironment.Y(); j < shipEnvironment.Y() + shipEnvironment.H(); j++) {
				int k = this.findPositionId(i, j);
				if(k != -1)
					this.positions.remove(k);
			}
		}
	}		
	
	private int findPositionId(int x, int y) {
		for(Position p : this.positions) {
			if(p.X() == x && p.Y() == y)
				return this.positions.indexOf(p);
		}
		
		return -1;
	}
		
	@Override
	public void sendData(IValueObject vo) {
		this.queue.add(MessageHelper.wrap(vo));
	}
}