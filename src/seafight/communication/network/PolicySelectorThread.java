package seafight.communication.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import seafight.helpers.FileHelper;

public class PolicySelectorThread extends AbstractSelectorThread {
	private static final Logger logger = Logger.getLogger(PolicySelectorThread.class);
	
	private static final String POLICY_REQUEST = "<policy-file-request/>\0";
	
	private ByteBuffer buffer = ByteBuffer.allocate(256).order(ByteOrder.LITTLE_ENDIAN);
	
	private String policyFile;
	
	public PolicySelectorThread() throws IOException {
		this.policyFile = FileHelper.GetFile("./conf/policy.xml") + "\0";
	}
	
	public String getPolicyFile() { return this.policyFile; }
	
	@Override
	protected void acceptConnection(SelectionKey selectionKey) throws IOException {
		ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
		SocketChannel sc = ssc.accept();
		sc.configureBlocking(false);		
		sc.register(this.selector, SelectionKey.OP_READ);

		logger.debug("Client[" + sc.socket().getInetAddress().getHostAddress() + ":" + sc.socket().getPort() + "] connected");		
	}

	@Override
	protected void closeConnection(SelectionKey selectionKey) throws IOException {
		SocketChannel channel = (SocketChannel) selectionKey.channel();
		
		logger.debug("Client[" + channel.socket().getInetAddress().getHostAddress() + ":" + channel.socket().getPort() + "] disconnected");
		
		selectionKey.channel().close();
		selectionKey.cancel();
	}

	@Override
	protected void read(SelectionKey selectionKey) throws IOException {
		SocketChannel channel = (SocketChannel) selectionKey.channel();
		
		buffer.clear();		
		int readBytes = channel.read(buffer);
				
		if(readBytes == -1) {
			this.closeConnection(selectionKey);
			return;
		}
		
		if(readBytes > 0) {
			logger.debug("Read " + readBytes + " bytes from client[" + channel.socket().getInetAddress().getHostAddress() + ":" + channel.socket().getPort() + "]");
			
			byte[] data = new byte[readBytes];
			System.arraycopy(buffer.array(), 0, data, 0, readBytes);
			
			if(POLICY_REQUEST.equalsIgnoreCase(new String(data)))
				selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
		}	
	}

	@Override
	protected void write(SelectionKey selectionKey) throws IOException {
		SocketChannel channel = (SocketChannel) selectionKey.channel();
		
		int writeBytes = channel.write(ByteBuffer.wrap(this.policyFile.getBytes()));
		
		logger.debug("Send " + writeBytes + " bytes to client[" +  channel.socket().getInetAddress().getHostAddress() + ":" + channel.socket().getPort() + "]");
		
		selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
	}
}