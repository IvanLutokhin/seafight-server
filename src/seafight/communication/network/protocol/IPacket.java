package seafight.communication.network.protocol;

import java.nio.ByteBuffer;

public interface IPacket {
	int getLength();
	
	int getPacketID();
	
	ByteBuffer getData();
	
	void setData(ByteBuffer data);
	
	ByteBuffer getBytes();
}