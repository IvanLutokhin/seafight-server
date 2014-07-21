package seafight.communication.network.protocol.sendpackets;

import seafight.communication.network.protocol.SendablePacket;
import seafight.communication.vo.DisconnectVO;

public class DisconnectPacket extends SendablePacket {
	@Override
	public void writeImpl() {
		this.writeByte(((DisconnectVO) this.vo).getReasonCode());
	}
}