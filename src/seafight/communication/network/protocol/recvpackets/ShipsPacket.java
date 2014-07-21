package seafight.communication.network.protocol.recvpackets;

import java.util.ArrayList;
import java.util.List;

import seafight.communication.ClientState;
import seafight.communication.network.protocol.ReceivablePacket;
import seafight.communication.vo.SystemMessageVO;
import seafight.mechanics.SystemMessage;
import seafight.mechanics.combat.Position;
import seafight.mechanics.combat.Ship;

public class ShipsPacket extends ReceivablePacket {
	private List<Ship> ships = new ArrayList<Ship>();
	
	public List<Ship> getShips() { return this.ships; }
	
	@Override
	public int getPacketID() { return 0x02; }
	
	@Override
	public void readImpl() {
		int count = this.readByte();
		for(int i = 0; i < count; i++) {
			int x = this.readByte();
			int y = this.readByte();
			int orientation = this.readByte();
			int size = this.readByte();
						
			this.ships.add(new Ship(new Position(x, y), size, orientation));
		}
	}

	@Override
	public void run() {
		this.getNetworkClient().changeClientState(ClientState.WAIT_ENEMY);
			
		this.getNetworkClient().getCombatContext().getCombatMap().reset();
		for(Ship ship : this.ships) {			
			if(!this.getNetworkClient().getCombatContext().getCombatMap().tryAddShip(ship)) {
				this.getNetworkClient().sendData(new SystemMessageVO(SystemMessage.ERROR_DOT_SHIP));
			}
		}				
	}
}