package seafight.communication.network.protocol.sendpackets;

import seafight.communication.network.protocol.SendablePacket;
import seafight.communication.vo.SystemMessageVO;

public class SystemMessagePacket extends SendablePacket {
	@Override
	public void writeImpl() {
		this.writeByte(((SystemMessageVO) this.vo).getMessageId());		
	}
}