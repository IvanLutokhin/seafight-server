package seafight.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import seafight.mechanics.combat.CellType;
import seafight.mechanics.combat.CombatMap;
import seafight.mechanics.combat.Position;
import seafight.mechanics.combat.Ship;
import seafight.mechanics.combat.ShipEnvironment;
import seafight.mechanics.combat.ShipOrientation;

public class MapHelper {
	public static ShipEnvironment getShipEnvironment(Ship ship) {
		return MapHelper.getShipEnvironment(ship.getPosition(), ship.getSize(), ship.getOrientation());
	}
	
	public static ShipEnvironment getShipEnvironment(Position position, int size, int orientation) {
		int x = (position.X() == 0) ? 0 : position.X() - 1;
		int y = (position.Y() == 0) ? 0 : position.Y() - 1;
		
		int w = 0; int h = 0;
						
		if(orientation == ShipOrientation.HORIZONTAL && ((position.X() + size) < CombatMap.N)) {						
			w = size + ((position.X() == 0 || (position.X() + size) == CombatMap.N) ? 1 : 2);
			h = (position.Y() == 0 || position.Y() == CombatMap.N - 1) ? 2 : 3;		
		}		
		else if(orientation == ShipOrientation.VERTICAL && (position.Y() + size < CombatMap.N)) {			
			w = (position.X() == 0 || position.X() == CombatMap.N - 1) ? 2 : 3;
			h = size + ((position.Y() == 0 || (position.Y() + size) == CombatMap.N) ? 1 : 2);			
		}
		
		return (w == 0 && h == 0) ? null : new ShipEnvironment(x, y, w, h);
	}
	
	public static List<Position> getShipCells(Ship ship) {
		return MapHelper.getShipCells(ship.getPosition(), ship.getSize(), ship.getOrientation());
	}
	
	public static List<Position> getShipCells(Position position, int size, int orientation) {
		ShipEnvironment environment = MapHelper.getShipEnvironment(position, size, orientation);
		if(environment == null)
			return null;
		
		List<Position> cells = new ArrayList<Position>();
		
		for(int i = environment.X(); i < environment.X() + environment.W(); i++) {
			for(int j = environment.Y(); j < environment.Y() + environment.H(); j++) {
				cells.add(new Position(i, j));
			}
		}
				
		return cells;
	}
	
	public static List<Position> getEnvShipCells(Ship ship) {
		List<Position> cells = MapHelper.getShipCells(ship);
		if(cells == null)
			return null;
		
		Iterator<Position> iterator = cells.iterator();
		while(iterator.hasNext()) {
			Position position = iterator.next();
			
			if(ship.isEquals(position))
				iterator.remove();
		}
		
		return cells;
	}
	
	public static boolean validateShipPosition(CombatMap combatMap, Ship ship) {
		return MapHelper.validateShipPosition(combatMap, ship.getPosition(), ship.getSize(), ship.getOrientation());
	}
	
	public static boolean validateShipPosition(CombatMap combatMap, Position position, int size, int orientation) {
		if(combatMap.getCellByPosition(position).getType() == CellType.SHIP)
			return false;
		
		List<Position> cells = MapHelper.getShipCells(position, size, orientation);
		if(cells == null)
			return false;
		
		for(Position p : cells) {
			if(combatMap.getCellByPosition(p).getType() != CellType.EMPTY)			
				return false;
		}
		
		return true;
	}	
}