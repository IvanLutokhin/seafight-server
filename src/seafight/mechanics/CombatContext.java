package seafight.mechanics;

import seafight.mechanics.combat.CombatMap;

public class CombatContext {
	private Room room;
	private CombatMap combatMap;
	
	public CombatContext(Room room, CombatMap combatMap) {
		this.room = room;
		this.combatMap = combatMap;
	}
	
	public Room getRoom() {
		return this.room;
	}
	
	public CombatMap getCombatMap() {
		return this.combatMap;
	}
}