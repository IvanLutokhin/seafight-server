package seafight.communication.network.protocol.sendpackets;

import seafight.communication.network.protocol.SendablePacket;
import seafight.communication.vo.ClientStateVO;

public class ClientStatePacket extends SendablePacket {		
	@Override
	public void writeImpl() {		
		this.writeByte(((ClientStateVO) this.vo).getClientState());
	}
}