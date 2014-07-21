package seafight.mechanics.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import seafight.helpers.MapHelper;

public class CombatMap {
	public static final int N = 10;
	
	public static final int SHIP_I_COUNT = 4;
	public static final int SHIP_II_COUNT = 3;
	public static final int SHIP_III_COUNT = 2;
	public static final int SHIP_IV_COUNT = 1;
	
	private Cell[][] cells = new Cell[N][N];
	private List<Ship> ships = Collections.synchronizedList(new ArrayList<Ship>());
	
	public CombatMap() {		
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				this.cells[i][j] = new Cell(new Position(j, i));				
			}
		}
	}
	
	public Cell getCellByPosition(Position position) { return this.cells[position.Y()][position.X()]; }
	
	public List<Ship> getShips() { return this.ships; }
	
	public Ship getShipByPosition(Position position) {
		for(Ship ship : this.ships) {
			if(ship.isEquals(position))
				return ship;
		}
		
		return null;
	}
	
	public boolean tryAddShip(Ship ship) {		
		if(MapHelper.validateShipPosition(this, ship)) {
			this.addShip(ship);
			this.ships.add(ship);
			return true;
		} else { return false; }		
	}	
	
	public void addShip(Ship ship) { this.addShip(ship.getPosition(), ship.getSize(), ship.getOrientation()); }
	
	public void addShip(Position position, int size, int orientation) {
		if(orientation == ShipOrientation.HORIZONTAL) {
			for(int i = position.X(); i < position.X() + size; i++)
				this.cells[position.Y()][i].setType(CellType.SHIP);
		}
		else if(orientation == ShipOrientation.VERTICAL) {
			for(int i = position.Y(); i < position.Y() + size; i++)
				this.cells[i][position.X()].setType(CellType.SHIP);
		}
	}
	
	public void removeShip(Ship ship) { this.removeShip(ship.getPosition(), ship.getSize(), ship.getOrientation()); }
	
	public void removeShip(Position position, int size, int orientation) {
		if(orientation == ShipOrientation.HORIZONTAL) {
			for(int i = position.X(); i < position.X() + size; i++)
				this.cells[position.Y()][i].setType(CellType.EMPTY);
		}
		else if(orientation == ShipOrientation.VERTICAL) {
			for(int i = position.Y(); i < position.Y() + size; i++)
				this.cells[i][position.X()].setType(CellType.EMPTY);
		}
	}
	
	public void reset() {
		this.ships.clear();
				
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				this.cells[i][j].setType(CellType.EMPTY);			
			}
		}		
	}
	
	public boolean isReady() {
		return (SHIP_I_COUNT + SHIP_II_COUNT + SHIP_III_COUNT + SHIP_IV_COUNT) == this.ships.size();
	}
	
	public boolean isAlive() {
		boolean alive = false;
		for(Ship ship : this.ships) {
			if(ship.isAlive()) {
				alive = true;
				break;
			}
		}
		
		return alive;
	}
}