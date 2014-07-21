package seafight.communication.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;

import org.apache.log4j.Logger;

public abstract class AbstractSelectorThread extends Thread {
	private static final Logger logger = Logger.getLogger(AbstractSelectorThread.class);
	
	protected String host;
	protected int port;
	protected boolean shutdown = true;
	
	protected Selector selector;
	
	public void bind(String host, int port) throws IOException {
		this.host = (host.equalsIgnoreCase("*")) ? "0.0.0.0" : host;
		this.port = port;
		this.shutdown = false;
		
		this.selector = Selector.open();
		
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.socket().bind(new InetSocketAddress(this.host, this.port));
		ssc.register(this.selector, ssc.validOps());
	}
	
	public String getHost() { return this.host; }
	
	public Integer getPort() { return this.port; }
	
	public Boolean isShutdown() { return this.shutdown; }
	
	public void shutdown() { this.shutdown = true; }
	
	@Override
	public void run() {
		while(!this.isShutdown()) {
			try { Thread.sleep(50);	}
			catch (InterruptedException e) { logger.error(e); }
									
			if(this.selector.isOpen()) {			
				try {
					if(this.selector.selectNow() > 0) {
						Set<SelectionKey> selectionKeys = this.selector.selectedKeys();
						
						for(SelectionKey selectionKey : selectionKeys) {
							if(!selectionKey.isValid()) { closeConnection(selectionKey); continue; }
							
							if(selectionKey.isValid() && selectionKey.isAcceptable()) { acceptConnection(selectionKey); }
							
							if(selectionKey.isValid() && selectionKey.isReadable()) { read(selectionKey); }
							
							if(selectionKey.isValid() && selectionKey.isWritable()) { write(selectionKey); }
						}
						
						selectionKeys.clear();
					}
				} catch (IOException e) { logger.error(e); }
			} else { break; }
		}			
	}
	
	protected abstract void acceptConnection(SelectionKey selectionKey) throws IOException;
	
	protected abstract void closeConnection(SelectionKey selectionKey) throws IOException;
	
	protected abstract void read(SelectionKey selectionKey) throws IOException;
	
	protected abstract void write(SelectionKey selectionKey) throws IOException;
}