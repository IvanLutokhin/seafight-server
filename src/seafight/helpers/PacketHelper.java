package seafight.helpers;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import seafight.communication.network.protocol.AbstractPacket;
import seafight.communication.network.protocol.ReceivablePacket;
import seafight.communication.network.protocol.SendablePacket;
import seafight.communication.network.protocol.recvpackets.*;
import seafight.communication.network.protocol.sendpackets.*;
import seafight.communication.vo.IValueObject;

public class PacketHelper {
	private static final Logger logger = Logger.getLogger(PacketHelper.class);
	
	public static SendablePacket wrap(IValueObject vo) {
		SendablePacket packet = null;
		
		switch(vo.getValueObjectID()) {
			case 0x01: { packet = new TurnStepPacket(); break; }
			case 0x02: { packet = new ShotResponsePacket(); break; }
			case 0x03: { packet = new ShipDeadPacket(); break; }
			case 0x7D: { packet = new SystemMessagePacket(); break; }
			case 0x7E: { packet = new ClientStatePacket(); break; }
			case 0x7F: { packet = new DisconnectPacket(); break; }
			default: { logger.error("Unknow ValueObject ID[" + vo.getValueObjectID() + "]"); }
		}
		
		if(packet != null)
			packet.setValueObject(vo);
		
		return packet;
	}
	
	public static ByteBuffer serialize(SendablePacket packet) {
		if(!packet.write()) {
			logger.warn("Cannot write packet ID: " + packet.getPacketID());
			return null;
		}
		
		return packet.getBytes();
	}
	
	public static ReceivablePacket unserialize(ByteBuffer byteBuffer) {
		int position = byteBuffer.position();
		
		if(byteBuffer.remaining() > AbstractPacket.PACKET_LENGTH_SIZE) {
			int length = byteBuffer.getShort() - AbstractPacket.PACKET_LENGTH_SIZE;
			
			if(length <= 0) return null;
								
			if(byteBuffer.remaining() >= length) {
				ReceivablePacket packet = null;
				
				int packetID = byteBuffer.get() & 0xFF;
				
				switch(packetID) {
					case 0x01: { packet = new FindEnemyPacket(); break; }
					case 0x02: { packet = new ShipsPacket(); break; }
					case 0x03: { packet = new ShotRequestPacket(); break; }
					default: {						
						byteBuffer.position(byteBuffer.position() + length - AbstractPacket.PACKET_ID_SIZE);												
						logger.warn("Unknown packet ID: " + packetID);
					}
				}
				
				if(packet != null) {
					packet.setData(byteBuffer);
					
					if(!packet.read()) {
						byteBuffer.position(byteBuffer.position() + length - AbstractPacket.PACKET_ID_SIZE);
						logger.warn("Cannot read packet ID: " + packet.getPacketID());
					}				
				}
								
				return packet;
			}
			
			byteBuffer.position(position);
		}
		
		return null;
	}
}