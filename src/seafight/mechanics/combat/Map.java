package seafight.mechanics.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Map {
	private static final int N = 10;
	
	private static final int MAX_ITERATION = 50;
	
	private static final int SHIP_I_COUNT = 4;
	private static final int SHIP_II_COUNT = 3;
	private static final int SHIP_III_COUNT = 2;
	private static final int SHIP_IV_COUNT = 1;
		
	private Cell[][] cells = new Cell[N][N];
	private List<Ship> ships = Collections.synchronizedList(new ArrayList<Ship>());
	private List<Position> positions = new ArrayList<Position>();
	
	public Map() {		
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				this.cells[i][j] = new Cell(new Position(j, i));
				this.positions.add(new Position(j, i));
			}
		}
	}
	
	public void reset() {
		this.ships.clear();
		this.positions.clear();
		
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				this.cells[i][j].setType(CellType.EMPTY);
				this.positions.add(new Position(j, i));
			}
		}		
	}
	
	public void resetPositions() {
		this.positions.clear();
		
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < N; j++) {				
				this.positions.add(new Position(j, i));
			}
		}	
	}
	
	public List<Position> getPositions() { return this.positions; }
	
	public List<Ship> getShips() { return this.ships; }
	
	public Ship getShipByPosition(Position position) {
		for(Ship ship : this.ships) {
			if(ship.isEquals(position))
				return ship;
		}
		
		return null;
	}
	
	public boolean addShip(Ship ship) { 
		if(this.validatePosition(ship)) {
			this.setShip(ship.getPosition(), ship.getSize(), ship.getOrientation());
			this.ships.add(ship);
			return true;
		} else { return false; }		
	}	
	
	public List<Position> getShipEnvironment(Ship ship) {
		List<Position> environment = this.getPositionEnvironment(ship);
		
		Iterator<Position> iterator = environment.iterator();
		while(iterator.hasNext()) {
			Position position = iterator.next();
			
			if(ship.isEquals(position))
				iterator.remove();
		}
		
		return environment;
	}
			
	public List<Position> getPositionEnvironment(Ship ship) { return this.getPositionEnvironment(ship.getPosition(), ship.getOrientation(), ship.getSize()); }
	
	public List<Position> getPositionEnvironment(Position position, int orientation, int size) {
		int x = (position.X() == 0) ? 0 : position.X() - 1;
		int y = (position.Y() == 0) ? 0 : position.Y() - 1;
		
		int w = 0; int h = 0;
		
		List<Position> environment = new ArrayList<Position>();
		
		if(orientation == ShipOrientation.HORIZONTAL && ((position.X() + size) < N)) {						
			w = size + ((position.X() == 0 || (position.X() + size) == N) ? 1 : 2);
			h = (position.Y() == 0 || position.Y() == N - 1) ? 2 : 3;		
		}		
		else if(orientation == ShipOrientation.VERTICAL && (position.Y() + size < N)) {			
			w = (position.X() == 0 || position.X() == N - 1) ? 2 : 3;
			h = size + ((position.Y() == 0 || (position.Y() + size) == N) ? 1 : 2);			
		}
		
		for(int i = x; i < x + w; i++) {
			for(int j = y; j < y + h; j++) {
				environment.add(new Position(i, j));
			}
		}
				
		return environment;
	}
	
	public boolean validatePosition(Ship ship) { return this.validatePosition(ship.getPosition(), ship.getOrientation(), ship.getSize()); }
	
	public boolean validatePosition(Position position, int orientation, int size) {
		if(this.cells[position.Y()][position.X()].getType() == CellType.SHIP)
			return false;
		
		List<Position> environment = this.getPositionEnvironment(position, orientation, size);
		
		for(Position p : environment) {
			if(this.cells[p.Y()][p.X()].getType() != CellType.EMPTY)
				return false;
		}
		
		return true;
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
	
	public int shot(Position position) {
		if(this.cells[position.Y()][position.X()].getType() == CellType.EMPTY) {
			this.cells[position.Y()][position.X()].setType(CellType.HIT);
			
			return CellType.HIT;
		}
		
		if(this.cells[position.Y()][position.X()].getType() == CellType.SHIP) {
			this.cells[position.Y()][position.X()].setType(CellType.DEAD);
			
			Ship ship = this.getShipByPosition(position);			
			ship.hit();
			
			return CellType.DEAD;
		}
		
		return -1;
	}

	public void autoDotShips() {
		this.reset();
		
		for(int i = 0; i < SHIP_IV_COUNT; i++)
			this.ships.add(new Ship(new Position(-1, -1), 4));
		
		for(int i = 0; i < SHIP_III_COUNT; i++)
			this.ships.add(new Ship(new Position(-1, -1), 3));
		
		for(int i = 0; i < SHIP_II_COUNT; i++)
			this.ships.add(new Ship(new Position(-1, -1), 2));
		
		for(int i = 0; i < SHIP_I_COUNT; i++)
			this.ships.add(new Ship(new Position(-1, -1), 1));
		
		for(int i = 0; i < this.ships.size(); i++) {
			if(!this.trySetShip(this.ships.get(i))) {					
				this.reset();
				i = -1;
				continue;
			}
		}
		
		this.resetPositions();
	}
	
	private boolean trySetShip(Ship ship) {	
		boolean complete = false;
		int iteration = 0;
		
		while(!complete) {
			if(this.positions.size() == 0)
				return false;
			
			int k = (int) (Math.random() * this.positions.size());
			
			Position position = this.positions.get(k);
										
			int orientation = (int) (Math.random() * 2);
			
			if(this.tryAddShip(position, ship.getSize(), orientation)) {
				/*ship.move(position);
				ship.rotate(orientation);	*/	
				complete = true;					
			}
			
			iteration++;
			if(iteration == MAX_ITERATION) return false;
		}
		
		return true;
	}
	
	private boolean tryAddShip(Position position, int size, int orientation) {	
		int x = (position.X() == 0) ? 0 : position.X() - 1;
		int y = (position.Y() == 0) ? 0 : position.Y() - 1;
								
		int w = 0; int h = 0;	
		
		if(orientation == ShipOrientation.HORIZONTAL && ((position.X() + size) < N)) {						
			w = size + ((position.X() == 0 || (position.X() + size) == N) ? 1 : 2);
			h = (position.Y() == 0 || position.Y() == N - 1) ? 2 : 3;				
		}		
		else if(orientation == ShipOrientation.VERTICAL && (position.Y() + size < N)) {			
			w = (position.X() == 0 || position.X() == N - 1) ? 2 : 3;
			h = size + ((position.Y() == 0 || (position.Y() + size) == N) ? 1 : 2);				
		} else { return false; }
		
		if(this.validatePosition(new Position(x, y), size, orientation)) {
			this.setShip(new Position(x, y), size, orientation);
						
			this.clearUsePosition(x, y, w, h);
			
			return true;
		}
		
		return false;
	}
	
	private void clearUsePosition(int x, int y, int w, int h) {
		for(int i = x; i < x + w; i++) {
			for(int j = y; j < y + h; j++) {
				int k = this.findPositionId(i, j);
				if(k != -1)
					this.positions.remove(k);
			}
		}
	}		
	
	private int findPositionId(int x, int y) {
		for(Position p : this.positions) {
			if(p.X() == x && p.Y() == y) return this.positions.indexOf(p);
		}
		
		return -1;
	}
	
	private void setShip(Position position, int size, int orientation) {		
		if(orientation == ShipOrientation.HORIZONTAL) {
			for(int i = position.X(); i < position.X() + size; i++)
				this.cells[position.Y()][i].setType(CellType.SHIP);
		}
		else if(orientation == ShipOrientation.VERTICAL) {
			for(int i = position.Y(); i < position.Y() + size; i++)
				this.cells[i][position.X()].setType(CellType.SHIP);
		}		
	}
}