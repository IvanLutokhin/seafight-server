package seafight.communication.contexts;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import seafight.communication.network.protocol.SendablePacket;
import seafight.communication.vo.IValueObject;
import seafight.helpers.PacketHelper;

public class NetworkContext implements ICommunicationContext {				
	private SelectionKey selectionKey;
	private SocketChannel socketChannel;
	
	private ByteBuffer byteBuffer = ByteBuffer.allocate(64 * 1024);
			
	private AtomicBoolean pendingWrite = new AtomicBoolean();
	private long pendingWriteTime;
	
	private Queue<SendablePacket> queue = new ConcurrentLinkedQueue<SendablePacket>();
	
	public NetworkContext(SelectionKey selectionKey) {
		this.selectionKey = selectionKey;
		this.socketChannel = (SocketChannel) selectionKey.channel();		
	}
	
	public SocketChannel getSocketChannel() { return this.socketChannel; }
	
	public String getHostAddress() { return this.socketChannel.socket().getInetAddress().getHostAddress(); }
	
	public int getPort() { return this.socketChannel.socket().getPort(); }
	
	public ByteBuffer getByteBuffer() { return this.byteBuffer; }	
	
	public boolean isPendingWrite() { return this.pendingWrite.get(); }
	
	public long getPendingWriteTime() { return this.pendingWriteTime; }
		
	public void disableWriteInterest() {
		if(this.pendingWrite.compareAndSet(true, false)) {
			this.selectionKey.interestOps(this.selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
		}	
	}
	
	public void enableWriteInterest() {
		if(this.pendingWrite.compareAndSet(false, true)) {
			this.selectionKey.interestOps(this.selectionKey.interestOps() | SelectionKey.OP_WRITE);
		}
	}
	
	public Queue<SendablePacket> getQueue() { return this.queue; }
	
	public void sendData(IValueObject vo) {
		if(this.socketChannel.isOpen()) {
			this.queue.add(PacketHelper.wrap(vo));			
			
			this.enableWriteInterest();
		}
	}
}