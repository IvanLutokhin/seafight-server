package seafight.communication.network.protocol.sendpackets;

import seafight.communication.network.protocol.SendablePacket;
import seafight.communication.vo.ShotResponseVO;

public class ShotResponsePacket extends SendablePacket {
	@Override
	public void writeImpl() {
		this.writeByte(((ShotResponseVO) this.vo).getPositionX());
		this.writeByte(((ShotResponseVO) this.vo).getPositionY());
		this.writeByte(((ShotResponseVO) this.vo).getCellType());				
	}
}