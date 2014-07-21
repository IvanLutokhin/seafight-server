package seafight.communication.network.protocol;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import seafight.communication.NetworkClient;

public abstract class ReceivablePacket extends AbstractPacket implements IReceivablePacket, Runnable {
	private static final Logger logger = Logger.getLogger(ReceivablePacket.class);
	
	protected ByteBuffer data;
	
	protected NetworkClient networkClient;
	
	public int getLength() { return PACKET_LENGTH_SIZE + PACKET_ID_SIZE + this.data.array().length; }
	
	public ByteBuffer getData() { return this.data; }
	
	public void setData(ByteBuffer data) { this.data = data; }
	
	public ByteBuffer getBytes() {
		ByteBuffer bytes = ByteBuffer.allocate(this.getLength());
		bytes.putShort((short) this.getLength());
		bytes.put((byte) this.getPacketID());
		bytes.put(this.data);
		
		return bytes;
	}
	
	public NetworkClient getNetworkClient() { return this.networkClient; }
	
	public void setNetworkClient(NetworkClient networkClient) { this.networkClient = networkClient; }
	
	public boolean read() {
		try { this.readImpl(); }
		catch (Exception e) { logger.error(e); return false; }
		
		return true;
	}
	
	protected int readByte() {
		return this.data.get() & 0xFF;
	}
	
	protected void readBytes(byte[] bytes) {
		this.data.get(bytes);
	}
	
	protected void readBytes(byte[] bytes, int offset, int length) {
		this.data.get(bytes, offset, length);
	}
	
	protected int readShort() {
		return this.data.getShort();
	}
	
	protected int readInt() {
		return this.data.getInt();
	}
	
	protected double readDouble() {
		return this.data.getDouble();
	}
	
	protected String readString() {
		int length = this.readShort();
		byte[] bytes = new byte[length];
		this.readBytes(bytes, 0, length);
		return new String(bytes);
	}
	
	protected boolean readBoolean() {
		return (this.readByte() == 0) ? false : true;
	}
}