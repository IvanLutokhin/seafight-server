package seafight.communication.network.protocol.sendpackets;

import seafight.communication.network.protocol.SendablePacket;
import seafight.communication.vo.ShipDeadVO;

public class ShipDeadPacket extends SendablePacket {
	@Override
	public void writeImpl() {
		this.writeByte(((ShipDeadVO) this.vo).getPositionX());
		this.writeByte(((ShipDeadVO) this.vo).getPositionY());
		this.writeByte(((ShipDeadVO) this.vo).getSize());
		this.writeByte(((ShipDeadVO) this.vo).getOrientation());
	}
}