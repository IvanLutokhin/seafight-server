package seafight.communication.network.protocol;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import seafight.communication.vo.IValueObject;

public abstract class SendablePacket extends AbstractPacket implements ISendablePacket {
	private static final Logger logger = Logger.getLogger(SendablePacket.class);
	
	protected IValueObject vo;
	
	protected List<Byte> data = new LinkedList<Byte>();
	
	public int getPacketID() { return this.vo.getValueObjectID(); }
	
	public int getLength() { return PACKET_LENGTH_SIZE + PACKET_ID_SIZE + this.data.size(); }
		
	public ByteBuffer getData() {
		ByteBuffer buffer = ByteBuffer.allocate(this.data.size());
		for(byte b : this.data)
			buffer.put(b);
		
		return buffer;
	}
	
	public void setData(ByteBuffer data) {
		for(byte b : data.array())
			this.data.add(b);
	}
	
	public ByteBuffer getBytes() {
		ByteBuffer bytes = ByteBuffer.allocate(this.getLength());
		bytes.putShort((short) this.getLength());
		bytes.put((byte) this.getPacketID());
		bytes.put(this.getData().array());
		
		return bytes;
	}
	
	public boolean write() {
		try { this.writeImpl(); }
		catch (Exception e) { logger.error(e); return false; }
		
		return true;
	}
	
	public IValueObject getValueObject() {
		return this.vo;
	}
	
	public void setValueObject(IValueObject vo) {
		this.vo = vo;
	}
	
	protected void writeByte(int value) {
		this.addByte((byte) value);
	}
	
	protected void writeBytes(byte[] value) {
		this.addBytes(value);
	}
	
	protected void writeBytes(byte[] value, int offset, int length) {
		this.addBytes(value, offset, length);
	}
	
	protected void writeShort(int value) {
		this.addBytes(ByteBuffer.allocate(2).putShort((short) value).array());
	}
	
	protected void writeInt(int value) {
		this.addBytes(ByteBuffer.allocate(4).putInt(value).array());
	}
	
	protected void writeDouble(double value) {
		this.addBytes(ByteBuffer.allocate(8).putDouble(value).array());
	}
	
	protected void writeString(String value) {
		this.writeShort(value.length());
		this.addBytes(value.getBytes());
	}
	
	protected void writeBoolean(boolean value) {		
		this.writeByte((byte) ((value) ? 1 : 0));
	}
	
	private void addByte(byte value) {
		this.data.add(value);
	}
	
	private void addBytes(byte[] value) {
		this.addBytes(value, 0, value.length);
	}
	
	private void addBytes(byte[] value, int offset, int length) {
		if((offset + length) > value.length) return;
		
		for(int i = offset; i < (offset + length); i++)
			this.data.add(value[i]);
	}
}