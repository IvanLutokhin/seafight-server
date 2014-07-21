package seafight.communication.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

import org.apache.log4j.Logger;

import seafight.communication.NetworkClient;
/*
import seafight.communication.protocol.ReceivablePacket;
import seafight.communication.protocol.SendablePacket;
import seafight.helper.PacketHelper;
import seafight.manager.ClientManager;
import seafight.manager.ThreadPoolManager;*/
import seafight.communication.contexts.NetworkContext;
import seafight.communication.network.protocol.ReceivablePacket;
import seafight.communication.network.protocol.SendablePacket;
import seafight.helpers.PacketHelper;
import seafight.managers.ThreadPoolManager;


public class MasterSelectorThread extends AbstractSelectorThread {
	private static final Logger logger = Logger.getLogger(MasterSelectorThread.class);
			
	@Override
	protected void acceptConnection(SelectionKey selectionKey) throws IOException {
		ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
		SocketChannel sc = ssc.accept();
		sc.configureBlocking(false);		
		SelectionKey clientKey = sc.register(this.selector, SelectionKey.OP_READ);
		NetworkClient client = new NetworkClient(new NetworkContext(clientKey));
		clientKey.attach(client);
		
		logger.debug("Client[" + sc.socket().getInetAddress().getHostAddress() + ":" + sc.socket().getPort() + "] connected");		
	}

	@Override
	protected void closeConnection(SelectionKey selectionKey) throws IOException {
		NetworkClient client = (NetworkClient) selectionKey.attachment();
		
		client.onDisconnect();
		
		logger.debug("Client[" + client.getNetworkContext().getHostAddress() + ":" + client.getNetworkContext().getPort() + "] disconnected");
		
		selectionKey.channel().close();
		selectionKey.cancel();		
	}

	@Override
	protected void read(SelectionKey selectionKey) throws IOException {
		NetworkClient client = (NetworkClient) selectionKey.attachment();
		
		ByteBuffer buffer = client.getNetworkContext().getByteBuffer();
		
		buffer.compact();
		
		int readBytes = client.getNetworkContext().getSocketChannel().read(buffer);
		
		if(readBytes == -1) {
			this.closeConnection(selectionKey);
			return;
		}
						
		if(readBytes > 0) {
			logger.debug("Read " + readBytes + " bytes from client[" + client.getNetworkContext().getHostAddress() + ":" + client.getNetworkContext().getPort() + "]");
			buffer.flip();

			while(buffer.hasRemaining()) {
				ReceivablePacket packet = PacketHelper.unserialize(buffer);
				if(packet != null) {
					logger.debug("Get packet ID[" + packet.getPacketID() + "] from client[" + client.getNetworkContext().getHostAddress() + ":" + client.getNetworkContext().getPort() + "]");
					
					packet.setNetworkClient(client);
					ThreadPoolManager.getInstance().execute(packet);
				}
			}		
		}		
	}

	@Override
	protected void write(SelectionKey selectionKey) throws IOException {
		NetworkClient client = (NetworkClient) selectionKey.attachment();
		
		Queue<SendablePacket> queue = client.getNetworkContext().getQueue();
		
		while(!queue.isEmpty()) {
			SendablePacket packet = queue.poll();
			ByteBuffer buffer = PacketHelper.serialize(packet);
			if(buffer != null) {
				buffer.flip();
				
				int writeBytes = client.getNetworkContext().getSocketChannel().write(buffer);				
				
				logger.debug("Write " + writeBytes + " bytes to client[" + client.getNetworkContext().getHostAddress() + ":" + client.getNetworkContext().getPort() + "]");
				logger.debug("Send packet ID[" + packet.getPacketID() + "] to client[" + client.getNetworkContext().getHostAddress() + ":" + client.getNetworkContext().getPort() + "]");				
			}
		}
				
		client.getNetworkContext().disableWriteInterest();
	}	
}