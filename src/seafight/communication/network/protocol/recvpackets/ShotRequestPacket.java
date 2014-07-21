package seafight.communication.network.protocol.recvpackets;

import seafight.communication.network.protocol.ReceivablePacket;

public class ShotRequestPacket extends ReceivablePacket {
	private int positionX;
	private int positionY;
	
	@Override
	public int getPacketID() { return 0x03; }
	
	@Override
	public void readImpl() {
		this.positionX = this.readByte();
		this.positionY = this.readByte();		
	}
	
	@Override
	public void run() {
		this.getNetworkClient().getCombatContext().getRoom().shot(this.getNetworkClient(), this.positionX, this.positionY);
	}
}