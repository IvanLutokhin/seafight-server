package seafight.communication.network.protocol;

public interface IReceivablePacket extends IPacket {
	void readImpl();
}