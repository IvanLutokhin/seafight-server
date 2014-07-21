package seafight.communication.network.protocol.recvpackets;

import seafight.communication.network.protocol.ReceivablePacket;
import seafight.managers.CombatManager;

public class FindEnemyPacket extends ReceivablePacket {
	@Override
	public int getPacketID() { return 0x01; }
	
	@Override
	public void readImpl() { }

	@Override
	public void run() {	
		CombatManager.getInstance().addFighterToQueue(this.getNetworkClient());
	}
}